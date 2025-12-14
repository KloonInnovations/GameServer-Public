package io.kloon.gameserver.modes.creative.menu.patterns;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.patterns.tinker.TinkerBlockMenu;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SelectBlockProxy implements ChestButton {
    private final ChestMenu parent;
    private final CreativeConsumer<Block> onSelect;
    private final Block block;

    public SelectBlockProxy(ChestMenu parent, CreativeConsumer<Block> onSelect, Block block) {
        this.parent = parent;
        this.onSelect = onSelect;
        this.block = block;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        boolean hasProperties = !block.properties().isEmpty();
        if (click.isRightClick() && hasProperties) {
            new TinkerBlockMenu(parent, block).withOnComplete(onSelect).display(p);
        } else {
            onSelect.accept(player, block);
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>\{BlockFmt.getName(block)}";

        Lore lore = new Lore();
        lore.add(MM."<dark_gray>ID \{block.key().value()}");
        lore.addEmpty();

        if (block.properties().isEmpty()) {
            lore.add("<cta>Click to pick this block!");
        } else {
            lore.add("<rcta>Click to tinker!");
            lore.add("<lcta>Click to pick this block!");
        }

        Material icon = block.registry().material();
        if (icon == null) {
            icon = Material.BEDROCK;
        }

        return MenuStack.of(icon, name, lore);
    }
}
