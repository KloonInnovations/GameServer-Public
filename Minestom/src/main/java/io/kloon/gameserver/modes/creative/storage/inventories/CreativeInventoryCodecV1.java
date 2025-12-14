package io.kloon.gameserver.modes.creative.storage.inventories;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.item.ItemStack;

import java.io.*;
import java.util.List;

public class CreativeInventoryCodecV1 implements MinecraftCodec<List<ItemStack>> {
    public static final byte VERSION = 2;

    public static final byte ITEM_TYPE_AIR = 0;
    public static final byte ITEM_TYPE_TOOL = 1;
    public static final byte ITEM_TYPE_NBT = 2;
    public static final byte ITEM_TYPE_TINKERED_BLOCK = 3;
    public static final byte ITEM_TYPE_PATTERN_BLOCK = 4;
    public static final byte ITEM_TYPE_MASK = 5;

    private final CreativeInvEncoderV1 encoder = new CreativeInvEncoderV1();
    private final CreativeInvDecoderV1 decoder;

    public CreativeInventoryCodecV1(CreativePlayer player) {
        this.decoder = new CreativeInvDecoderV1(player);
    }

    @Override
    public void encode(List<ItemStack> items, MinecraftOutputStream out) throws IOException {
        encoder.encode(items, out);
    }

    @Override
    public List<ItemStack> decode(MinecraftInputStream in) throws IOException {
        return decoder.decode(in);
    }

    public int getVersion() {
        return VERSION;
    }
}
