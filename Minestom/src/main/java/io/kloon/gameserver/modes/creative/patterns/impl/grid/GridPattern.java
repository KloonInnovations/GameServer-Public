package io.kloon.gameserver.modes.creative.patterns.impl.grid;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.PatternType;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class GridPattern extends CreativePattern {
    private final CreativePattern lines;
    private final CreativePattern inBetween;
    private final Map<Axis, GridAxis> axis;
    private final boolean open;

    public GridPattern(CreativePattern lines, CreativePattern inBetween, Map<Axis, GridAxis> axis, boolean open) {
        super(PatternType.GRID);
        this.lines = lines;
        this.inBetween = inBetween;
        this.axis = new HashMap<>(axis);
        this.open = open;
    }

    public GridPattern() {
        super(PatternType.GRID);
        this.lines = new SingleBlockPattern(Block.STONE);
        this.inBetween = new SingleBlockPattern(Block.AIR);
        this.axis = new HashMap<>();
        for (Axis axe : Axis.values()) {
            axis.put(axe, GridAxis.createDefault(axe));
        }
        this.open = true;
    }

    public CreativePattern getLines() {
        return lines;
    }

    public GridPattern withLines(CreativePattern lines) {
        return new GridPattern(lines, inBetween, axis, open);
    }

    public CreativePattern getInBetween() {
        return inBetween;
    }

    public GridPattern withInBetween(CreativePattern inBetween) {
        return new GridPattern(lines, inBetween, axis, open);
    }

    public GridAxis getAxis(Axis axis) {
        return this.axis.computeIfAbsent(axis, GridAxis::createDefault);
    }

    public GridPattern withAxis(GridAxis gridAxis) {
        Map<Axis, GridAxis> axisCopy = new HashMap<>(this.axis);
        axisCopy.put(gridAxis.axis(), gridAxis);
        return new GridPattern(lines, inBetween, axisCopy, open);
    }

    public boolean isOpen() {
        return open;
    }

    public GridPattern withOpen(boolean open) {
        return new GridPattern(lines, inBetween, axis, open);
    }

    @Override
    public String labelMM() {
        return "Grid";
    }

    @Override
    public Lore lore() {
        Lore lore = new Lore();
        lore.add(MM."<gray>Lines: \{lines.labelMM()}");
        lore.add(MM."<gray>In-between: \{inBetween.labelMM()}");
        return lore;
    }

    @Override
    public CreativePattern compute(Instance instance, Point blockPos) {
        int linesCrossed = 0;
        for (Axis axe : Axis.values()) {
            GridAxis gridAxis = axis.computeIfAbsent(axe, GridAxis::createDefault);
            if (gridAxis.isOnTheLine(blockPos)) {
                ++linesCrossed;
            }
        }

        if (linesCrossed == 0) {
            return inBetween;
        }

        int required = open ? 2 : 1;
        return linesCrossed >= required ? lines : inBetween;
    }

    @Override
    public CreativePattern copy() {
        return new GridPattern(lines.copy(), inBetween.copy(), axis, open);
    }

    @Override
    public boolean canBePickedUp() {
        return true;
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<GridPattern> {
        @Override
        public void encode(GridPattern pattern, MinecraftOutputStream out) throws IOException {
            out.write(pattern.lines, CreativePattern.CODEC);
            out.write(pattern.inBetween, CreativePattern.CODEC);
            for (Axis axis : Axis.values()) {
                GridAxis gridAxis = pattern.axis.computeIfAbsent(axis, GridAxis::createDefault);
                out.write(gridAxis, GridAxis.CODEC);
            }
            out.writeBoolean(pattern.open);
        }

        @Override
        public GridPattern decode(MinecraftInputStream in) throws IOException {
            CreativePattern lines = in.read(CreativePattern.CODEC);
            CreativePattern inBetween = in.read(CreativePattern.CODEC);
            Map<Axis, GridAxis> axises = new HashMap<>(3);
            for (Axis axis : Axis.values()) {
                GridAxis gridAxis = in.read(GridAxis.CODEC);
                axises.put(axis, gridAxis);
            }
            boolean open = in.readBoolean();
            return new GridPattern(lines, inBetween, axises, open);
        }
    }
}
