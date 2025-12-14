package io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.manage;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.WaypointMenu;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class RenameWaypointButton implements ChestButton {
    private final WaypointMenu menu;
    private final WaypointStorage waypoint;

    private static final Material SIGN = Material.MANGROVE_SIGN;

    public RenameWaypointButton(WaypointMenu menu, WaypointStorage waypoint) {
        this.menu = menu;
        this.waypoint = waypoint;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        String beforeMM = waypoint.getNameMM();

        String[] inputLines = SignUX.inputLines("Waypoint name", "goes there");
        SignUX.display(player, SIGN.block(), inputLines, input -> {
            waypoint.withName(input[0]);
            String afterMM = waypoint.getNameMM();

            player.broadcast().send(MsgCat.TOOL,
                    NamedTextColor.GREEN, "RENAMED!", MM."<gray>From \{beforeMM} <gray>to \{afterMM}<gray>!",
                    SoundEvent.ENTITY_VILLAGER_WORK_CARTOGRAPHER, 1.4f);

            menu.reload();
            menu.display(player);
        });
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Rename";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<gray>Name: \{waypoint.getNameMM()}");
        if (ThreadLocalRandom.current().nextInt(10) == 0) {
            lore.add(MM."<dark_gray>Fancy!");
        }
        lore.add(Component.empty());

        lore.add(MM."<yellow>Click to edit!");

        return MenuStack.of(SIGN).name(name).lore(lore).build();
    }
}
