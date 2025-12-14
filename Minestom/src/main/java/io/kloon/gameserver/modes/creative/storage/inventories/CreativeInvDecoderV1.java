package io.kloon.gameserver.modes.creative.storage.inventories;

import com.github.luben.zstd.ZstdInputStream;
import io.kloon.gameserver.minestom.io.MinecraftDecoder;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.inventories.items.CreativeItemDecoderV1;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolsListener;
import io.kloon.gameserver.modes.creative.tools.generics.NotImplementedTool;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.item.ItemStack;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.modes.creative.storage.inventories.CreativeInventoryCodecV1.*;

public class CreativeInvDecoderV1 implements MinecraftDecoder<List<ItemStack>> {
    private final CreativeItemDecoderV1 itemDecoder;

    public CreativeInvDecoderV1(CreativePlayer player) {
        this.itemDecoder = new CreativeItemDecoderV1(player);
    }

    @Override
    public List<ItemStack> decode(MinecraftInputStream in) throws IOException {
        ZstdInputStream compressionStream = new ZstdInputStream(in);
        MinecraftInputStream dataStream = new MinecraftInputStream(compressionStream);

        byte version = dataStream.readByte();
        if (version != VERSION) {
            throw new IllegalStateException(STR."version mismatch, expected \{VERSION} but found \{version}");
        }

        int itemsCount = dataStream.readShort();
        List<ItemStack> items = new ArrayList<>(itemsCount);
        for (int i = 0; i < itemsCount; ++i) {
            ItemStack item = itemDecoder.decode(dataStream);
            items.add(item);
        }
        return items;
    }
}
