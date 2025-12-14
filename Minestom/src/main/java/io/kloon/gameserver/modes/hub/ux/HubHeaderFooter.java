package io.kloon.gameserver.modes.hub.ux;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.ux.headerfooter.KloonHeaderFooter;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class HubHeaderFooter extends KloonHeaderFooter {
    public HubHeaderFooter(KloonPlayer player) {
        super(player);
    }

    @Override
    public List<Component> renderHeader() {
        return Collections.singletonList(MM."<gray>This is a header");
    }

    @Override
    public List<Component> renderFooter() {
        Lore lore = new Lore();

        addPingLore(lore);
        lore.addEmpty();

        lore.add("<#FF266E>You're in the hub!");

        return lore.asList();
    }
}
