package io.kloon.gameserver.modes.creative.menu.worldadmin.time;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.autoupdate.AutoUpdateButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CurrentTimeButton implements ChestButton, AutoUpdateButton {
    private long renderedTime = 0;

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        p.closeInventory();

        String url = "https://minecraft.wiki/w/Daylight_cycle";
        player.sendPit(NamedTextColor.GREEN, "TIME?", MM."<gray>Click this to learn how it works!"
                .hoverEvent(MM."<yellow>\{url}")
                .clickEvent(ClickEvent.openUrl(url)));
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Time Info";

        Lore lore = new Lore();
        lore.wrap("<gray>A Minecraft day lasts 20 minutes and its time is recorded with a number between <green>0 and 24000<gray>, where 0 is 6 AM.");
        lore.addEmpty();

        this.renderedTime = player.getInstance().getTime();
        lore.add(MM."<gray>Current time: <green>\{renderedTime % 24_000}");
        lore.addEmpty();

        lore.add("<cta>Click for more info!");

        return MenuStack.of(Material.CLOCK, name, lore);
    }

    @Override
    public boolean shouldRerender(Player player) {
        return player.getInstance().getTime() != renderedTime;
    }
}
