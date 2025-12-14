package io.kloon.gameserver.modes.creative.patterns.impl.grid.menu;

import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.impl.grid.GridAxis;
import io.kloon.gameserver.modes.creative.patterns.impl.grid.GridPattern;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.menu.CubeDimensionButton;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.util.coordinates.Axis;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class AxisToggleButton extends ToggleButton {
    private final GridPatternMenu menu;
    private final Axis axis;

    private static final ToolDataType DATA_TYPE = ToolDataType.PATTERN_BOUND;

    public AxisToggleButton(GridPatternMenu menu, Axis axis) {
        this.menu = menu;
        this.axis = axis;
    }

    @Override
    public boolean isEnabled(Player player) {
        return menu.getPattern().getAxis(axis).enabled();
    }

    @Override
    public void setEnabled(Player p, boolean enabled) {
        CreativePlayer player = (CreativePlayer) p;
        GridPattern pattern = menu.getPattern();
        GridAxis gridAxis = pattern.getAxis(axis).withEnabled(enabled);
        pattern = pattern.withAxis(gridAxis);
        menu.updateAndDisplay(player, pattern);
    }

    @Override
    public void onValueChange(Player p, boolean newValue) {
        CreativePlayer player = (CreativePlayer) p;
        Component msg = newValue
                ? MM."<gray>Toggled \{getName()} to <green>ON<gray>!"
                : MM."<gray>Toggled \{getName()} to <red>OFF<gray>!";

        double pitch = newValue ? 1.9 : 1.5;
        DATA_TYPE.sendMsg(player, msg, SoundEvent.BLOCK_NOTE_BLOCK_PLING, pitch);

        menu.reload().display(player);
    }

    @Override
    public ItemBuilder2 getIcon(boolean enabled) {
        HeadProfile headProfile = CubeDimensionButton.getHead(axis);
        return MenuStack.ofHead(headProfile);
    }

    @Override
    public Material getIconMaterial(boolean enabled) {
        return Material.WOODEN_AXE;
    }

    @Override
    public String getName() {
        return STR."\{axis.name()} Axis";
    }

    @Override
    public List<Component> getDescription() {
        return MM_WRAP."<gray>Zoop.";
    }
}
