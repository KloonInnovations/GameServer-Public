package io.kloon.gameserver.modes.creative.patterns.impl;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.PatternType;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;

import java.io.IOException;

public class PassthroughPattern extends CreativePattern {
    public PassthroughPattern() {
        super(PatternType.PASSTHROUGH);
    }

    @Override
    public String labelMM() {
        return "<white>Passthrough";
    }

    @Override
    public Lore lore() {
        return new Lore().add("<#FF1769>Leaves blocks as-is!");
    }

    @Override
    public CreativePattern compute(Instance instance, Point blockPos) {
        return new SingleBlockPattern(instance.getBlock(blockPos));
    }

    @Override
    public CreativePattern copy() {
        return new PassthroughPattern();
    }

    @Override
    public boolean canBePickedUp() {
        return true;
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<PassthroughPattern> {
        @Override
        public void encode(PassthroughPattern obj, MinecraftOutputStream out) throws IOException {

        }

        @Override
        public PassthroughPattern decode(MinecraftInputStream in) throws IOException {
            return new PassthroughPattern();
        }
    }
}
