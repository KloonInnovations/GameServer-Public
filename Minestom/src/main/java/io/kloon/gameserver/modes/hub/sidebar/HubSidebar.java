package io.kloon.gameserver.modes.hub.sidebar;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.KloonInstance;
import io.kloon.gameserver.minestom.components.ComponentWrapper;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.ux.sidebar.KloonSidebar;
import io.kloon.infra.facts.KloonDataCenter;
import net.kyori.adventure.text.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class HubSidebar extends KloonSidebar {
    private final KloonPlayer player;

    public HubSidebar(KloonPlayer player) {
        super(player);
        this.player = player;
    }

    DateTimeFormatter DATE_TIME_FMT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("h:mm:ss a")
            .toFormatter(Locale.US);

    @Override
    public List<Component> renderLines() {
        List<Component> lines = new ArrayList<>();

        KloonInstance instance = player.getInstance();

        lines.add(MM."<dark_gray>\{instance.getCuteName()}");
        lines.add(Component.empty());

        lines.addAll(ComponentWrapper.wrap(MM."<white>Welcome to the hub!", 20));
        lines.add(Component.empty());

        lines.addAll(ComponentWrapper.wrap(MM."<#F99AE5>If we're lucky, this text will be updated.", 22));
        lines.add(Component.empty());

        KloonDataCenter datacenter = Kgs.getInfra().datacenter();
        if (datacenter == KloonDataCenter.EU_WEST) {
            ZonedDateTime time = ZonedDateTime.now(ZoneId.of("Europe/London"));
            lines.add(MM."<white>London Time: <#FF266E>\{time.format(DATE_TIME_FMT)}");
        } else {
            ZonedDateTime time = ZonedDateTime.now(ZoneId.of("America/New_York"));
            lines.add(MM."<white>NYC Time: <#FF266E>\{time.format(DATE_TIME_FMT)}");
        }

        return lines;
    }
}