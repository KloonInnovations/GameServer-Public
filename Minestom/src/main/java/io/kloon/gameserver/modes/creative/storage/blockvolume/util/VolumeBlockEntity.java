package io.kloon.gameserver.modes.creative.storage.blockvolume.util;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public record VolumeBlockEntity(
        String id,
        CompoundBinaryTag nbt
) {
    public Block inject(Block block) {
        BlockHandler handler = MinecraftServer.getBlockManager().getHandlerOrDummy(id);
        return block.withHandler(handler).withNbt(nbt);
    }

    public static VolumeBlockEntity fromBlock(Block block, BlockHandler handler) {
        CompoundBinaryTag nbt = block.nbt();
        nbt = nbt == null ? CompoundBinaryTag.empty() : nbt;
        return new VolumeBlockEntity(handler.getKey().asString(), nbt);
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<VolumeBlockEntity> {
        @Override
        public void encode(VolumeBlockEntity obj, MinecraftOutputStream out) throws IOException {
            out.writeString(obj.id());
            out.writeNbt(obj.nbt());
        }

        @Override
        public VolumeBlockEntity decode(MinecraftInputStream in) throws IOException {
            return new VolumeBlockEntity(
                    in.readString(),
                    in.readNbt()
            );
        }
    }

    public static final MapCodec MAP_CODEC = new MapCodec();
    public static class MapCodec implements MinecraftCodec<Map<RelativePos, VolumeBlockEntity>> {
        @Override
        public void encode(Map<RelativePos, VolumeBlockEntity> blockEntities, MinecraftOutputStream out) throws IOException {
            out.writeVarInt(blockEntities.size());
            for (Map.Entry<RelativePos, VolumeBlockEntity> entry : blockEntities.entrySet()) {
                RelativePos relativePos = entry.getKey();
                VolumeBlockEntity blockEntity = entry.getValue();

                out.write(relativePos, RelativePos.CODEC);
                out.write(blockEntity, VolumeBlockEntity.CODEC);
            }
        }

        @Override
        public Map<RelativePos, VolumeBlockEntity> decode(MinecraftInputStream in) throws IOException {
            int blockEntitiesCount = in.readVarInt();
            Map<RelativePos, VolumeBlockEntity> blockEntities = new HashMap<>(blockEntitiesCount);
            for (int i = 0; i < blockEntitiesCount; ++i) {
                RelativePos relPos = in.read(RelativePos.CODEC);
                VolumeBlockEntity blockEntity = in.read(VolumeBlockEntity.CODEC);
                blockEntities.put(relPos, blockEntity);
            }
            return blockEntities;
        }
    }
}
