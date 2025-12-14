package io.kloon.gameserver.modes.creative.patterns.impl.grid.menu;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.PatternNumberButton;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.grid.GridAxis;
import io.kloon.gameserver.modes.creative.patterns.impl.grid.GridPattern;
import io.kloon.gameserver.modes.creative.patterns.menu.EditPatternMenu;
import io.kloon.gameserver.modes.creative.patterns.menu.PatternWithinPattern;
import io.kloon.gameserver.modes.creative.patterns.menu.SelectPatternWithinPatternButton;
import io.kloon.gameserver.modes.creative.patterns.menu.toggle.PatternToggleButton;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import io.kloon.gameserver.util.coordinates.Axis;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class GridPatternMenu extends EditPatternMenu<GridPattern> {
    public static final PatternWithinPattern<GridPattern> LINES = new PatternWithinPattern<>(
            Material.NETHER_BRICK_FENCE, "Lines",
            MM_WRAP."<gray>What blokck or pattern is used for the lines of the grid.",
            GridPattern::getLines, GridPattern::withLines);

    public static final PatternWithinPattern<GridPattern> IN_BETWEEN = new PatternWithinPattern<>(
            Material.CHISELED_NETHER_BRICKS, "In-Between",
            MM_WRAP."<gray>What block or pattern is used to fill in-between the lines of the grid.",
            GridPattern::getInBetween, GridPattern::withInBetween);

    public static final ToolToggle<GridPattern> OPEN = new ToolToggle<>(
            Material.IRON_TRAPDOOR, "Open",
            MM_WRAP."<gray>If open, line blocks will be ignored when the block is only sitting on one axis.",
            GridPattern::isOpen, GridPattern::withOpen);

    public GridPatternMenu(ChestMenu parent, GridPattern pattern, CreativeConsumer<CreativePattern> update) {
        super(parent, pattern, update);
    }

    @Override
    protected void registerButtons() {
        reg(10, new SelectPatternWithinPatternButton<>(this, LINES));
        reg(11, new SelectPatternWithinPatternButton<>(this, IN_BETWEEN));
        reg(12, new SwapGridPatternsButton(this));

        reg(29, new PatternToggleButton<>(this, OPEN));

        reg(14, new AxisToggleButton(this, Axis.X));
        if (pattern.getAxis(Axis.X).enabled()) {
            reg(15, slot -> new PatternNumberButton<>(slot, this, axisSpacing(Axis.X)));
            reg(16, slot -> new PatternNumberButton<>(slot, this, axisOffset(Axis.X)));
        }

        reg(23, new AxisToggleButton(this, Axis.Y));
        if (pattern.getAxis(Axis.Y).enabled()) {
            reg(24, slot -> new PatternNumberButton<>(slot, this, axisSpacing(Axis.Y)));
            reg(25, slot -> new PatternNumberButton<>(slot, this, axisOffset(Axis.Y)));
        }

        reg(32, new AxisToggleButton(this, Axis.Z));
        if (pattern.getAxis(Axis.Z).enabled()) {
            reg(33, slot -> new PatternNumberButton<>(slot, this, axisSpacing(Axis.Z)));
            reg(34, slot -> new PatternNumberButton<>(slot, this, axisOffset(Axis.Z)));
        }

        reg(42, new SetAllAxisSpacingButton(this));
    }

    private static NumberInput<GridPattern> axisSpacing(Axis axis) {
        return NumberInput.functionInt(
                Material.VINE, NamedTextColor.AQUA,
                STR."\{axis.name()} Spacing",
                MM_WRAP."<gray>How many blocks in-between each line on the \{axis.name()} axis.",
                GridAxis.DEFAULT_SPACING, 1, GridAxis.MAX_SPACING,
                pattern -> pattern.getAxis(axis).spacing(),
                (pattern, value) -> {
                    GridAxis gridAxis = pattern.getAxis(axis).withSpacing(value);
                    return pattern.withAxis(gridAxis);
                }
        );
    }

    private static NumberInput<GridPattern> axisOffset(Axis axis) {
        return NumberInput.functionInt(
                Material.BAMBOO, NamedTextColor.AQUA,
                STR."\{axis.name()} Offset",
                MM_WRAP."<gray>Offsets the spacing on the \{axis.name()} axis.",
                GridAxis.DEFAULT_OFFSET, 1, GridAxis.MAX_OFFSET,
                pattern -> pattern.getAxis(axis).offset(),
                (pattern, value) -> {
                    GridAxis gridAxis = pattern.getAxis(axis).withOffset(value);
                    return pattern.withAxis(gridAxis);
                }
        );
    }
}
