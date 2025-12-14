package io.kloon.gameserver.modes.creative.tools.impl.laser;

import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.data.ItemBoundPattern;
import io.kloon.gameserver.modes.creative.tools.impl.laser.menu.LaserToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.LaserModeType;
import io.kloon.gameserver.modes.creative.tools.impl.laser.work.LaserGenSettings;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public class LaserToolSettings implements ItemBoundPattern {
    private byte[] pattern;
    private String modeDbKey = LaserModeType.CUBE.getDbKey();
    private int radius = LaserGenSettings.DEFAULT_RADIUS;
    private double offset = LaserToolMenu.LASER_OFFSET.defaultValue();

    @Override
    public boolean hasPattern() {
        return pattern != null;
    }

    @Override
    public @Nullable CreativePattern getPattern() {
        if (pattern == null) return new SingleBlockPattern(Block.STONE);
        return MinecraftInputStream.fromBytesSneaky(pattern, CreativePattern.CODEC);
    }

    @Override
    public void setPattern(CreativePattern pattern) {
        this.pattern = MinecraftOutputStream.toBytesSneaky(pattern, CreativePattern.CODEC);
    }

    public LaserModeType getMode() {
        return LaserModeType.BY_DB_KEY.get(modeDbKey, LaserModeType.SPHERE);
    }

    public void setMode(LaserModeType mode) {
        this.modeDbKey = mode.getDbKey();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public LaserGenSettings createGenSettings(Vec start, Vec end, boolean ignoreBlocks, MaskLookup mask) {
        LaserModeType mode = getMode();
        double offset = this.offset;
        if (mode.is3d()) {
            offset += radius;
        }

        return new LaserGenSettings(
                getPattern(),
                mask,
                start,
                end,
                mode,
                radius,
                offset,
                ignoreBlocks
        );
    }
}
