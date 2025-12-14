package io.kloon.gameserver.modes.creative.menu.worldadmin.time;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.world.TimeCommand;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CustomTimeButton implements ChestButton {
    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        int min = 0;
        int max = 24000;

        String[] displayLines = SignUX.inputLines("Enter time", min, max, NumberFmt.NO_DECIMAL);
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(min, max).build(player, input -> {
            TimeCommand.setTime(player, input.intValue());
        }));
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Custom Time";

        Lore lore = new Lore();
        lore.wrap(MM."<gray>Set a specific time for this world, between 0 and 24000");
        lore.addEmpty();
        lore.add(MM."<cta>Click to enter time!");

        return MenuStack.of(Material.OAK_SIGN, name, lore);
    }
}
