package io.kloon.gameserver.ux.headerfooter;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.MiniMessageTemplate;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.player.proxyinfo.KloonProxyInfo;
import io.kloon.infra.facts.KloonDataCenter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.minestom.server.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class KloonHeaderFooter {
    private static final Logger LOG = LoggerFactory.getLogger(KloonHeaderFooter.class);

    protected final KloonPlayer player;

    private Component header;
    private Component footer;

    public KloonHeaderFooter(KloonPlayer player) {
        this.player = player;
    }

    public final void tick() {
        if (GlobalMinestomTicker.getTick() % 5 != 0) {
            return;
        }

        List<Component> headerList;
        List<Component> footerList;

        try {
            headerList = renderHeader();
            if (headerList == null) {
                headerList = new ArrayList<>();
            }
        } catch (Throwable t) {
            LOG.error("Error rendering player-list header", t);
            headerList = Collections.singletonList(MM."<red>Error rendering header");
        }

        try {
            footerList = renderFooter();
            if (footerList == null) {
                footerList = new ArrayList<>();
            }
        } catch (Throwable t) {
            LOG.error("Error rendering player-list footer", t);
            footerList = Collections.singletonList(MM."<red>Error rendering footer");
        }

        Component newHeader = combineAsNewlines(headerList);
        Component newFooter = combineAsNewlines(footerList);

        if (!Objects.equals(header, newHeader) || !Objects.equals(footer, newFooter)) {
            player.sendPlayerListHeaderAndFooter(newHeader, newFooter);
        }

        header = newHeader;
        footer = newFooter;
    }

    private Component combineAsNewlines(List<Component> listOfComponents) {
        if (listOfComponents.isEmpty()) {
            return Component.empty();
        }

        Component component = listOfComponents.getFirst();
        for (int i = 1; i < listOfComponents.size(); ++i) {
            TextComponent line = Component.newline().style(Style.empty()).append(listOfComponents.get(i));
            component = component.append(line);
        }
        return component;
    }

    private static final String PING = "\uD83D\uDEDC";
    private static final String PROXY = "\uD83D\uDD0C";
    private static final String SERVER = "\uD83C\uDFAE";
    protected final void addPingLore(Lore lore) {
        KloonProxyInfo proxyInfo = player.getProxyInfo();
        String proxyLabel = proxyInfo.datacenter().getShortLabel().toUpperCase();

        KloonDataCenter datacenter = Kgs.getInfra().datacenter();
        String dcLabel = datacenter.getShortLabel().toUpperCase();

        String colorHex = MiniMessageTemplate.INFRA_COLOR.asHexString();
        lore.add(MM."<#FF266E>\{PING} <\{colorHex}>\{player.getLatency()}ms <dark_gray>| <#FF266E>\{PROXY} <\{colorHex}>\{proxyLabel} <dark_gray>| <#FF266E>\{SERVER} <\{colorHex}>\{dcLabel}");
    }

    public abstract List<Component> renderHeader();

    public abstract List<Component> renderFooter();
}
