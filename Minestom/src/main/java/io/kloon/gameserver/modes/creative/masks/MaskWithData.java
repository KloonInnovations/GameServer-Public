package io.kloon.gameserver.modes.creative.masks;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.nbt.NBT;
import io.kloon.gameserver.util.serialization.ObjectIdMcCodec;
import io.kloon.infra.util.codecs.Codec;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.bson.types.ObjectId;

import java.io.IOException;

public record MaskWithData<Data>(
        ObjectId id,
        MaskType<Data> type,
        boolean negated,
        Data data
) {
    public MaskWithData<Data> withNegated(boolean negated) {
        return new MaskWithData<>(id, type, negated, data);
    }

    public static final NbtCodec NBT_CODEC = new NbtCodec();
    public static class NbtCodec implements Codec<MaskWithData, CompoundBinaryTag> {
        @Override
        public CompoundBinaryTag encode(MaskWithData mask) {
            return NBT.compound(c -> {
                c.putString("id", mask.id().toHexString());
                c.putString("type", mask.type().getDbKey());
                c.putBoolean("negate", mask.negated());

                String dataJson = MaskType.DATA_GSON.toJson(mask.data());
                c.putString("data", dataJson);
            });
        }

        @Override
        public MaskWithData decode(CompoundBinaryTag nbt) {
            String idHex = nbt.getString("id");
            ObjectId id = new ObjectId(idHex);

            String typeKey = nbt.getString("type");
            MaskType<?> maskType = MaskTypes.get(typeKey);

            boolean negated = nbt.getBoolean("negate", false);

            String dataJson = nbt.getString("data");
            Object ruleData = maskType.getData(dataJson);

            return new MaskWithData(id, maskType, negated, ruleData);
        }
    }

    public static final McCodec MC_CODEC = new McCodec();
    public static class McCodec implements MinecraftCodec<MaskWithData> {
        @Override
        public void encode(MaskWithData mask, MinecraftOutputStream out) throws IOException {
            out.write(mask.id(), ObjectIdMcCodec.INSTANCE);
            out.writeUTF(mask.type().getDbKey());
            out.writeBoolean(mask.negated());
            String dataJson = MaskType.DATA_GSON.toJson(mask.data());
            out.writeUTF(dataJson);
        }

        @Override
        public MaskWithData decode(MinecraftInputStream in) throws IOException {
            ObjectId id = in.read(ObjectIdMcCodec.INSTANCE);
            String maskTypeDbKey = in.readUTF();
            MaskType<?> maskType = MaskTypes.get(maskTypeDbKey);

            boolean negated = in.readBoolean();

            String dataJson = in.readUTF();
            Object data = maskType.getData(dataJson);

            return new MaskWithData(id, maskType, negated, data);
        }
    }
}
