package io.kloon.gameserver.modes.creative.tools.impl.replace.menu.entry;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.patterns.BlockSelectionMenu;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.tools.impl.replace.menu.ReplacementEntryMenu;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementConfig;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ReplacingButton implements ChestButton {
    private final ReplacementEntryMenu menu;

    public ReplacingButton(ReplacementEntryMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        new BlockSelectionMenu(menu, this::onSelectBlock).display(player);
    }

    private void onSelectBlock(CreativePlayer player, Block block) {
        ReplacementConfig replacementConfig = menu.getReplacementConfig();
        CreativePattern existing = replacementConfig.get(block);
        if (existing != null) {
            player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 1.2f);
            player.sendPit(NamedTextColor.RED, "ALREADY EXISTS!", MM."<gray>There's an existing replacement entry for this block!");
            menu.display(player);
            return;
        }

        menu.setReplacing(block);
        menu.display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>What's Being Replaced";
        Lore lore = new Lore();

        Block replacing = menu.getReplacing();
        if (replacing == null) {
            lore.wrap("<gray>What block will be replaced.");
            lore.addEmpty();
            lore.add("<cta>Click to pick block!");
            return MenuStack.of(Material.MAGMA_CREAM, name, lore);
        }

        TinkeredBlock tinkered = new TinkeredBlock(replacing, menu.isReplaceOnExactState());

        lore.addEmpty();
        lore.add(MM."<gray>Block: \{tinkered.getNameMM()}");
        if (menu.isReplaceOnExactState()) {
            lore.add(tinkered.propertiesLore());
        }

        lore.addEmpty();
        lore.add("<cta>Click to switch block!");

        Material icon = replacing.registry().material();
        icon = icon == null ? Material.MAGMA_CREAM : icon;

        return MenuStack.of(icon, name, lore);
    }
}
