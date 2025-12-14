package io.kloon.gameserver.modes.creative.tools.snipe.settings.menu;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.snipe.SnipeCommand;
import io.kloon.gameserver.modes.creative.commands.snipe.SnipeIgnoreCommand;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggle;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggleButton;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.anchors.RangeAnchorsProxy;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class SnipeSettingsMenu extends ChestMenu {
    public static final String ICON = "\uD83D\uDEF0"; // 🛰

    private final ChestMenu parent;

    public static final PlayerStorageToggle IGNORE_BLOCKS = new PlayerStorageToggle(
            Material.GLASS, null, "Ignore Blocks",
            MM_WRAP."<gray>Does the snipe ignore blocks and is <green>always at max range<gray>.",
            SnipeIgnoreCommand.FULL,
            storage -> storage.getSnipe().isIgnoreBlocks(),
            (storage, value) -> storage.getSnipe().setIgnoreBlocks(value));

    public SnipeSettingsMenu(ChestMenu parent) {
        super("Snipe Settings", ChestSize.FOUR);
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        reg(11, RangeInputButton::new);
        reg(13, slot -> new PlayerStorageToggleButton(slot, IGNORE_BLOCKS));
        reg(15, new RangeAnchorsProxy(this));

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<light_purple>\{ICON} <title>Snipe Settings";

        Lore lore = new Lore();
        lore.add(ToolDataType.PLAYER_BOUND.getLoreSubtitle());
        lore.add(MM."<cmd>\{SnipeCommand.LABEL}");
        lore.addEmpty();
        lore.wrap("<gray>Change how you select and pick blocks at range when using tools.");
        lore.addEmpty();

        double range = player.getSnipe().getRange();
        lore.add(MM."<gray>Range: <green>\{NumberFmt.NO_DECIMAL.format(range)} blocks");

        lore.addEmpty();
        lore.add("<cta>Click to edit!");

        return MenuStack.of(Material.BREEZE_ROD, name, lore);
    }
}
