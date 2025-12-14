package io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.anchors;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class RangeAnchorsProxy implements ChestButton {
    private final ChestMenu parent;

    public RangeAnchorsProxy(ChestMenu parent) {
        this.parent = parent;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        new RangeAnchorsMenu(parent, player).display(player);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<title>Range Anchors";

        Lore lore = new Lore();
        lore.wrap("<gray>Set predefined ranges to cycle through for any tool with range.");
        lore.addEmpty();
        lore.wrap("<light_purple>Use Q (drop) to snap to the closest or next range!");
        lore.add("<dark_gray>Ctrl+Q (stack drop) for previous!");
        lore.addEmpty();

        lore.add("<gray>Ranges");
        List<Double> anchors = player.getCreativeStorage().getSnipe().getRangeAnchors();
        for (int i = 0; i < anchors.size(); i++) {
            double anchor = anchors.get(i);
            lore.add(MM."<dark_gray> \{i + 1}. <green>\{NumberFmt.NO_DECIMAL.format(anchor)}");
        }

        lore.addEmpty();
        lore.add("<cta>Click to customize!");

        return MenuStack.of(Material.ANVIL, name, lore);
    }
}
