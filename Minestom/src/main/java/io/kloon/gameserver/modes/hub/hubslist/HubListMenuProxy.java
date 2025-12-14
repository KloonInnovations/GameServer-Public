package io.kloon.gameserver.modes.hub.hubslist;

import io.kloon.bigbackend.games.hub.HubsList;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.autoupdate.NatsSubCache;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class HubListMenuProxy implements ChestButton {
    private static final Logger LOG = LoggerFactory.getLogger(HubListMenuProxy.class);

    private final ChestMenu parent;
    private final NatsSubCache<HubsList> hubsCache;

    public HubListMenuProxy(ChestMenu parent, NatsSubCache<HubsList> hubsCache) {
        this.parent = parent;
        this.hubsCache = hubsCache;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        hubsCache.fetchAndGet().orTimeout(1, TimeUnit.SECONDS).whenCompleteAsync((_, t) -> {
            if (t != null) {
                player.sendMessage(MM."<red>There was an error fetching the hubs listing!");
                LOG.error("Error fetching hubs listing", t);
                return;
            }

            ChestMenu listingMenu = new HubsListMenu(parent, hubsCache).display(player);
            hubsCache.sub(player, listingMenu);
        }, player.scheduler());
    }

    @Override
    public ItemStack renderButton(Player player) {
        List<Component> lore = MM_WRAP."<gray>There are multiple copies of the hub and you may switch between them to find your friends.";
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to view lobbies!");

        return MenuStack.of(Material.CRIMSON_DOOR).name(MM."<title>Hub Instances").lore(lore).build();
    }
}
