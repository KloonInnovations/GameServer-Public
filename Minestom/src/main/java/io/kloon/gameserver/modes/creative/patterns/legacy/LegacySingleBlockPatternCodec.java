package io.kloon.gameserver.modes.creative.patterns.legacy;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import net.minestom.server.instance.block.Block;

import java.io.IOException;

public class LegacySingleBlockPatternCodec implements MinecraftCodec<SingleBlockPattern> {
    public static final LegacySingleBlockPatternCodec INSTANCE = new LegacySingleBlockPatternCodec();

    @Override
    public void encode(SingleBlockPattern pattern, MinecraftOutputStream out) throws IOException {
        DumbPalette.BLOCK_CODEC_NO_TILE.encode(pattern.getBlock(), out);
    }

    @Override
    public SingleBlockPattern decode(MinecraftInputStream in) throws IOException {
        Block block = DumbPalette.BLOCK_CODEC_NO_TILE.decode(in);
        return new SingleBlockPattern(block);
    }
}
