package io.kloon.gameserver.modes.creative.tools.impl.laser.menu;

import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleButton;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.ItemBoundNumberButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.impl.laser.LaserToolSettings;
import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.LaserModeType;
import io.kloon.gameserver.modes.creative.tools.impl.laser.LaserTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolPatternSelectionButton;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class LaserToolMenu extends CreativeToolMenu<LaserTool> {
    private final ToolPatternSelectionButton patternSelect;

    private static final int PATTERN_SELECT_SLOT = 10;

    public static final NumberInput<LaserToolSettings> LASER_RADIUS = NumberInput.consumerInt(
            Material.NAUTILUS_SHELL, NamedTextColor.AQUA,
            "Radius",
            MM_WRAP."<gray>How many blocks to spawn around the laser.",
            1, 1, 7,
            LaserToolSettings::getRadius, LaserToolSettings::setRadius);

    public static final NumberInput<LaserToolSettings> LASER_OFFSET = NumberInput.consumer(
            Material.VAULT, NamedTextColor.GREEN,
            "Offset",
            MM_WRAP."<gray>How many blocks in front of your eyes the laser starts.",
            2, 1, 9,
            LaserToolSettings::getOffset, LaserToolSettings::setOffset);

    public LaserToolMenu(LaserTool tool, ItemRef itemRef) {
        super(tool, itemRef);
        this.patternSelect = new ToolPatternSelectionButton(this, PATTERN_SELECT_SLOT);
    }

    @Override
    protected void registerButtons() {
        reg(PATTERN_SELECT_SLOT, slot -> new ToolPatternSelectionButton(this, slot));

        LaserToolSettings settings = tool.getItemBound(itemRef);

        Cycle<LaserModeType> cycle = new Cycle<>(LaserModeType.values());
        cycle.select(settings.getMode());
        reg(12, slot -> new CycleButton<>(cycle, slot)
                .withIcon(Material.WARPED_FUNGUS)
                .withTitle(MM."<title>Laser Mode")
                .withDescription(MM_WRAP."<gray>What kind of shape is generated on each point along the laser.")
                .withOnClick((p, mode) -> {
                    CreativePlayer player = (CreativePlayer) p;
                    tool.editItemBound(player, itemRef, s -> s.setMode(mode));

                    player.msg().send(MsgCat.TOOL, NamedTextColor.RED,
                            "LASER MODE!", MM."<gray>Switched to \{mode.label()}!",
                            SoundEvent.ENTITY_MOOSHROOM_CONVERT, Pitch.rng(1.85, 0.15), 0.4);
                }));

        reg(14, slot -> new ItemBoundNumberButton<>(slot, this, LASER_RADIUS).withCloseOnSet());
        reg(16, slot -> new ItemBoundNumberButton<>(slot, this, LASER_OFFSET));

        reg(31, new SnipeSettingsMenu(this));

        reg().toolCommands(this);
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        patternSelect.clickPlayerInventory(event);
    }
}