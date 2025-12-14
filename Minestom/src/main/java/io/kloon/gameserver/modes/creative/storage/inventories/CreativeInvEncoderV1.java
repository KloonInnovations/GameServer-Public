package io.kloon.gameserver.modes.creative.storage.inventories;

import com.github.luben.zstd.ZstdOutputStream;
import io.kloon.gameserver.minestom.io.MinecraftEncoder;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.storage.inventories.items.CreativeItemEncoderV1;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import static io.kloon.gameserver.modes.creative.storage.inventories.CreativeInventoryCodecV1.*;

public class CreativeInvEncoderV1 implements MinecraftEncoder<List<ItemStack>> {
    private static final CreativeItemEncoderV1 ITEM_ENCODER = CreativeItemEncoderV1.INSTANCE;

    @Override
    public void encode(List<ItemStack> items, MinecraftOutputStream out) throws IOException {
        ZstdOutputStream compressionStream = new ZstdOutputStream(out);
        MinecraftOutputStream dataStream = new MinecraftOutputStream(compressionStream);

        dataStream.writeByte(VERSION);
        dataStream.writeShort(items.size());
        for (int i = 0; i < items.size(); ++i) {
            ItemStack item = items.get(i);
            ITEM_ENCODER.encode(item, dataStream);
        }

        dataStream.flush();
    }


}
