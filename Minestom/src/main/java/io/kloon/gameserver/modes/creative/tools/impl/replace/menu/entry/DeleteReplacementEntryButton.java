package io.kloon.gameserver.modes.creative.tools.impl.replace.menu.entry;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.replace.menu.ReplacementEntryMenu;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementConfig;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class DeleteReplacementEntryButton implements ChestButton {
    private final ReplacementEntryMenu menu;

    public DeleteReplacementEntryButton(ReplacementEntryMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        if (!shouldDisplay()) {
            return;
        }

        CreativePlayer player = (CreativePlayer) p;

        Block replacing = menu.getReplacing();
        if (replacing == null) {
            player.sendMessage(MM."<red>Can't delete what doesn't exist!");
            player.closeInventory();
            return;
        }

        ReplacementConfig config = menu.getReplacementConfig();
        boolean exact = menu.isReplaceOnExactState();

        config.remove(replacing, exact);
        menu.setReplacementConfig(player, config);

        menu.getParent().reload().display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        if (!shouldDisplay()) {
            return ItemStack.AIR;
        }

        Component name = MM."<title>Delete Entry";

        Lore lore = new Lore();
        lore.wrap("<gray>Remove this entry from the replacement config.");
        lore.addEmpty();
        lore.add("<cta>Click to delete!");

        return MenuStack.of(Material.TNT, name, lore);
    }

    private boolean shouldDisplay() {
        ReplacementConfig config = menu.getParent().getReplacementConfig();
        return config.getAsList().size() > 1;
    }
}
