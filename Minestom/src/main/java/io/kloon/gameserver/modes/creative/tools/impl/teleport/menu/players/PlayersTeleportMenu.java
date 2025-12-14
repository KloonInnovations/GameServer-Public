package io.kloon.gameserver.modes.creative.tools.impl.teleport.menu.players;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.autoupdate.AutoUpdateMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.pagination.MenuPagination;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class PlayersTeleportMenu extends ChestMenu implements AutoUpdateMenu {
    private final ChestMenu parent;
    private final CreativePlayer player;

    private final MenuPagination pagination;

    public PlayersTeleportMenu(ChestMenu parent, CreativePlayer player) {
        super("Teleport to Player", ChestSize.SIX);
        this.parent = parent;
        this.player = player;
        this.pagination = new MenuPagination(this, ChestLayouts.INSIDE);
    }

    @Override
    protected void registerButtons() {
        List<TeleportToPlayerButton> teleportButtons = getTargetPlayers(player).stream()
                .map(p -> new TeleportToPlayerButton(p.getUuid()))
                .toList();
        if (teleportButtons.isEmpty()) {
            reg(size.middleCenter(), MenuStack.of(Material.ORANGE_STAINED_GLASS)
                    .name(MM."<red>Where is everyone?").lore(MM_WRAP."<gray>There's no one in this list anymore. They probably logged off.")
                    .buildButton());
        } else {
            pagination.distribute(teleportButtons, this::reg);
        }

        reg().goBack(parent);
    }

    @Override
    public TaskSchedule getAutoUpdatePeriod() {
        return TaskSchedule.tick(20);
    }

    public static List<Player> getTargetPlayers(Player player) {
        return player.getInstance().getPlayers().stream()
                .filter(p -> p != player)
                .toList();
    }
}
