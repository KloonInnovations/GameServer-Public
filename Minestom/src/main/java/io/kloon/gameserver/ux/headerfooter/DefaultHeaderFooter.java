package io.kloon.gameserver.ux.headerfooter;

import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.Collections;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class DefaultHeaderFooter extends KloonHeaderFooter {
    public DefaultHeaderFooter(KloonPlayer player) {
        super(player);
    }

    @Override
    public List<Component> renderHeader() {
        return Collections.singletonList(MM."<gray>This is a header");
    }

    @Override
    public List<Component> renderFooter() {
        return Collections.singletonList(MM."<gray>This is a footer");
    }
}
