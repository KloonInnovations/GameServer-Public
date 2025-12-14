package io.kloon.gameserver.ux.sidebar;

import io.kloon.gameserver.minestom.KloonInstance;
import io.kloon.gameserver.minestom.components.ComponentWrapper;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class DefaultSidebar extends KloonSidebar {
    private final KloonPlayer player;

    public DefaultSidebar(KloonPlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    public List<Component> renderLines() {
        List<Component> lines = new ArrayList<>();

        KloonInstance instance = player.getInstance();

        lines.add(MM."<dark_gray>\{instance.getCuteName()}");
        lines.add(Component.empty());

        lines.addAll(ComponentWrapper.wrap(MM."<white>This is the default sidebar. Someone should edit it.", 20));

        return lines;
    }
}
