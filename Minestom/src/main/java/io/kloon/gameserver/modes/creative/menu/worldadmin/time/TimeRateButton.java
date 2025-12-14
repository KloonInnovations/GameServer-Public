package io.kloon.gameserver.modes.creative.menu.worldadmin.time;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.world.TimeCommand;
import io.kloon.gameserver.modes.creative.storage.datainworld.world.CreativeTimeStorage;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class TimeRateButton implements ChestButton {
    private final int slot;

    public TimeRateButton(int slot) {
        this.slot = slot;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        if (click.isRightClick() && player.getInstance().getWorldStorage().getTime().getTimeRate() != 0) {
            TimeCommand.setRate(player, 0);
            ChestMenuInv.rerender(p);
            return;
        }

        String[] displayLines = SignUX.inputLines("Enter Rate", "of time/s");
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(-24_000, 24_000).build(player, timeRate -> {
            timeRate /= 20;
            TimeCommand.setRate(player, timeRate);
        }));
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<title>Time Rate";

        Lore lore = new Lore();
        lore.wrap("<gray>How much <green>time <gray>is added every second.");
        lore.add("<dark_gray>Accepts decimals and negatives!");
        lore.addEmpty();

        double rate = player.getInstance().getWorldStorage().getTime().getTimeRate();
        if (rate == 0.0) {
            lore.add(MM."<gray>Time: <aqua>Frozen!");
        } else {
            lore.add(MM."<gray>Rate: <aqua>\{NumberFmt.TWO_DECIMAL.format(rate * 20)}/s");
        }
        if (rate != CreativeTimeStorage.DEFAULT_RATE) {
            lore.add(MM."<dark_gray>Default = \{NumberFmt.ONE_DECIMAL.format(CreativeTimeStorage.DEFAULT_RATE_PER_SECOND)}");
        }
        lore.addEmpty();

        if (rate == 0) {
            lore.add(MM."<cta>Click to set rate!");
        } else {
            lore.add(MM."<rcta>Click to freeze!");
            lore.add(MM."<lcta>Click to set rate!");
        }

        return MenuStack.of(Material.HONEYCOMB, name, lore);
    }
}
