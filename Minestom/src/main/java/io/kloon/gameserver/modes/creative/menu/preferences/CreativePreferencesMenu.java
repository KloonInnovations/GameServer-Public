package io.kloon.gameserver.modes.creative.menu.preferences;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.NightVisionCommand;
import io.kloon.gameserver.modes.creative.commands.WorldBorderCommand;
import io.kloon.gameserver.modes.creative.menu.preferences.messaging.MessagingPreferencesMenu;
import io.kloon.gameserver.modes.creative.menu.preferences.messaging.MessagingPreferencesProxy;
import io.kloon.gameserver.modes.creative.menu.preferences.selectioncolor.SelectionColorsMenu;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggle;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggleButton;
import io.kloon.gameserver.modes.creative.storage.playerdata.CreativePlayerStorage;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import io.kloon.gameserver.player.settings.menu.GeneralSettingsMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class CreativePreferencesMenu extends ChestMenu {
    public static final String ICON = "\uD83D\uDEDD"; // 🛝

    private final ChestMenu parent;
    private final boolean canRightClick;

    public static final PlayerStorageToggle NIGHT_VISION_TOGGLE = new PlayerStorageToggle(
            Material.SOUL_LANTERN, "<dark_purple>\uD83E\uDD87", "Night Vision", // 🦇
            MM_WRAP."<gray>Applies the Night Vision potion effect. Really helps to see stuff.",
            NightVisionCommand.SHORT_LABEL,
            CreativePlayerStorage::hasNightVision, CreativePlayerStorage::setNightVision);

    public static final PlayerStorageToggle RENDER_WORLD_BORDER = new PlayerStorageToggle(
            Material.SOUL_CAMPFIRE, "<aqua>\uD83C\uDF10", "World Border", // 🌐
            MM_WRAP."<gray>Renders the world border, showing the limit of where you can build.",
            WorldBorderCommand.LABEL,
            CreativePlayerStorage::isRenderingWorldBorder, CreativePlayerStorage::setRenderingWorldBorder
    );

    public static final PlayerStorageToggle SPAWN_ON_LAST_LOCATION = new PlayerStorageToggle(
            Material.RED_BED, "<red>\uD83D\uDC23", "Spawn on Last Location", // 🐣
            MM_WRAP."<gray>On worlds you have permission to edit, spawn at your last location instead of the world spawn.",
            null,
            CreativePlayerStorage::isSpawningOnLastLocation, CreativePlayerStorage::setSpawningOnLastLocation
    );

    public CreativePreferencesMenu(ChestMenu parent, boolean canRightClick) {
        super(STR."\{ICON} Player Preferences");
        this.parent = parent;
        this.canRightClick = canRightClick;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        if (canRightClick && click.isRightClick()) {
            new GeneralSettingsMenu(parent).display(player);
            return;
        }

        super.clickButton(player, click);
    }

    @Override
    protected void registerButtons() {
        reg(10, FlySpeedButton::new);
        reg(11, WalkSpeedButton::new);
        reg(12, SpeedEffectButton::new);
        reg(13, slot -> new PlayerStorageToggleButton(slot, NIGHT_VISION_TOGGLE) {
            @Override
            public void onValueChange(Player p, boolean nightVision) {
                super.onValueChange(p, nightVision);
                NightVisionCommand.applyNightVision((CreativePlayer) p);
            }
        });

        reg(15, new SnipeSettingsMenu(this));
        reg(16, new MessagingPreferencesProxy(this));

        reg(32, slot -> new PlayerStorageToggleButton(slot, SPAWN_ON_LAST_LOCATION));

        reg(34, slot -> new PlayerStorageToggleButton(slot, RENDER_WORLD_BORDER) {
            @Override
            public void onValueChange(Player p, boolean render) {
                super.onValueChange(p, render);
                WorldBorderCommand.applyWorldBorder((CreativePlayer) p);
            }
        });

        reg(28, HandBufferingTimeButton::new);
        reg(30, new SelectionColorsMenu(this));

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<dark_green>\{ICON} <title>Player Preferences";

        Lore lore = new Lore();
        lore.add("<dark_gray>Creative Settings");
        lore.addEmpty();

        lore.wrap("<gray>These settings persist across the creative mode.");
        lore.addEmpty();

        if (canRightClick) {
            lore.add("<rcta>Click for general!");
            lore.add("<lcta>Click to tune creative!");
        } else {
            lore.add("<cta>Click to tune!");
        }

        return MenuStack.of(Material.LIME_CANDLE, name, lore);
    }
}
