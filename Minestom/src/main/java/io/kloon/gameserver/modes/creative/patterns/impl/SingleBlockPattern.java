package io.kloon.gameserver.modes.creative.patterns.impl;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.PatternType;
import io.kloon.gameserver.modes.creative.patterns.legacy.LegacySingleBlockPatternCodec;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.io.IOException;

public class SingleBlockPattern extends CreativePattern {
    private final Block block;

    public SingleBlockPattern(Block block) {
        super(PatternType.SINGLE_BLOCK);
        this.block = block;
    }

    @Override
    public String labelMM() {
        return TinkeredBlock.getNameMM(block);
    }

    @Override
    public Lore lore() {
        return new Lore().add(labelMM());
    }

    @Override
    public ItemBuilder2 icon() {
        Material mat = block.registry().material();
        if (block == Block.AIR) {
            mat = Material.MILK_BUCKET;
        }
        mat = mat == null ? Material.BEDROCK : mat;
        return new ItemBuilder2(mat);
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public String getTypeName() {
        if (TinkeredBlock.is(block)) {
            return "Tinkered Block";
        }
        return super.getTypeName();
    }

    @Override
    public CreativePattern compute(Instance instance, Point blockPos) {
        return this;
    }

    @Override
    public boolean canBePickedUp() {
        return true;
    }

    @Override
    public CreativePattern copy() {
        return new SingleBlockPattern(block);
    }

    public static final Codec CODEC = new Codec();
    public static final MinecraftCodec<SingleBlockPattern> OLD_CODEC = LegacySingleBlockPatternCodec.INSTANCE;
    public static class Codec implements MinecraftCodec<SingleBlockPattern> {
        @Override
        public void encode(SingleBlockPattern pattern, MinecraftOutputStream out) throws IOException {
            DumbPalette.BLOCK_CODEC_FULL.encode(pattern.getBlock(), out);
        }

        @Override
        public SingleBlockPattern decode(MinecraftInputStream in) throws IOException {
            Block block = DumbPalette.BLOCK_CODEC_FULL.decode(in);
            return new SingleBlockPattern(block);
        }
    }
}
