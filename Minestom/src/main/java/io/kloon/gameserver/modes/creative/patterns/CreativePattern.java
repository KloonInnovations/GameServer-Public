package io.kloon.gameserver.modes.creative.patterns;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.io.IOException;

public abstract class CreativePattern {
    private final PatternType type;

    public static final int MAX_RECURSION = 5;

    public CreativePattern(PatternType type) {
        this.type = type;
    }

    public abstract String labelMM();

    public abstract Lore lore();

    public ItemBuilder2 icon() {
        return type.icon();
    }

    public String getTypeName() {
        return getType().getName();
    }

    public final Block computeBlock(Instance instance, Point blockPos) {
        CreativePattern pattern = compute(instance, blockPos);
        int recursions = 1;
        while (recursions < MAX_RECURSION) {
            if (pattern instanceof SingleBlockPattern single) {
                return single.getBlock();
            }

            pattern = pattern.compute(instance, blockPos);
            ++recursions;
        }
        throw new IllegalStateException("Too much recursion in pattern compute");
    }

    public abstract CreativePattern compute(Instance instance, Point blockPos);

    public abstract CreativePattern copy();

    public final PatternType getType() {
        return type;
    }

    public final boolean hasEditMenu() {
        return type.hasEditMenu();
    }

    public abstract boolean canBePickedUp();

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<CreativePattern> {
        @Override
        public void encode(CreativePattern pattern, MinecraftOutputStream out) throws IOException {
            PatternType type = pattern.type;
            out.writeVarInt(type.getDbKey());
            MinecraftCodec codec = type.getCodec();
            codec.encode(pattern, out);
        }

        @Override
        public CreativePattern decode(MinecraftInputStream in) throws IOException {
            int typeId = in.readVarInt();
            PatternType patternType = PatternType.BY_DB_KEY.get(typeId);
            if (patternType == null) {
                throw new IOException(STR."Unknown pattern type for id \{typeId}");
            }
            return patternType.getCodec().decode(in);
        }
    }
}
