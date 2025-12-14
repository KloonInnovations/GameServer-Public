package io.kloon.gameserver.modes.creative.menu.patterns;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.commands.patterns.PatternCommand;
import io.kloon.gameserver.modes.creative.menu.patterns.use.ChoosePatternMenu;
import io.kloon.gameserver.modes.creative.patterns.blocks.PatternBlock;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PatternSelectionProxy implements ChestButton {
    public static final String ICON = "\uD83C\uDFA8"; // 🎨

    private final ChestMenu parent;

    public PatternSelectionProxy(ChestMenu parent) {
        this.parent = parent;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        new ChoosePatternMenu(parent, (player, pattern) -> {
            ItemStack item = new PatternBlock(pattern).toItem();
            player.getInventoryExtras().grab(item);

            player.msg().send(MsgCat.INVENTORY,
                    NamedTextColor.GREEN, "PICKED UP!", MM."<gray>Added \{pattern.labelMM()} <gray>to your inventory!",
                    SoundEvent.BLOCK_BEACON_ACTIVATE, Pitch.rng(1.5, 0.5));
        }).display(p);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<light_purple>\{ICON} <title>Block Patterns";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{PatternCommand.LABEL}");
        lore.addEmpty();

        lore.wrap("<gray>Use patterns to apply logic to what blocks your tools actually create.");
        lore.addEmpty();
        lore.add("<cta>Click to browse!");

        return MenuStack.of(Material.PINK_GLAZED_TERRACOTTA, name, lore);
    }
}
