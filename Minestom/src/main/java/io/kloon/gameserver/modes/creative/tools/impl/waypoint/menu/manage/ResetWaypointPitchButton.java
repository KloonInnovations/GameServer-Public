package io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.manage;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class ResetWaypointPitchButton implements ChestButton {
    private final int slot;
    private final WaypointStorage waypoint;

    public ResetWaypointPitchButton(int slot, WaypointStorage waypoint) {
        this.slot = slot;
        this.waypoint = waypoint;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        if (waypoint.getPosition().pitch() == 0) {
            return;
        }


        Pos position = waypoint.getPosition();
        waypoint.withPosition(position.withPitch(0));

        player.broadcast().send(MsgCat.TOOL, NamedTextColor.GREEN, "RESET", MM."<gray>The pitch of \{waypoint.getNameMM()} <gray>has been reset!");
        ChestMenuInv.rerenderButton(slot, p);
    }

    @Override
    public ItemStack renderButton(Player player) {
        if (waypoint.getPosition().pitch() == 0) {
            return ItemStack.AIR;
        }

        Component name = MM."<title>Reset Pitch";

        List<Component> lore = MM_WRAP."<gray>Sets the pitch (how it looks up and down) of the way point to 0.";
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to reset!");

        return MenuStack.of(Material.SPYGLASS).name(name).lore(lore).build();
    }
}
