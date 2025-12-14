package io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.manage;

import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.RespawnCommand;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToggleWorldSpawnButton extends ToggleButton {
    private final WaypointStorage waypoint;
    private final int slot;

    public ToggleWorldSpawnButton(WaypointStorage waypoint, int slot) {
        this.waypoint = waypoint;
        this.slot = slot;
    }

    @Override
    public boolean isEnabled(Player player) {
        return waypoint.isWorldSpawn();
    }

    @Override
    public void setEnabled(Player p, boolean enabled) {
        CreativePlayer player = (CreativePlayer) p;
        CreativeInstance instance = player.getInstance();

        if (enabled) {
            instance.getWorldStorage().getWaypoints().getList().forEach(waypoint -> {
                waypoint.withWorldSpawn(false);
            });
        }

        waypoint.withWorldSpawn(enabled);
    }

    @Override
    public void onValueChange(Player p, boolean newValue) {
        CreativePlayer player = (CreativePlayer) p;
        ChestMenuInv.rerenderButton(slot, player);

        if (newValue) {
            player.broadcast().send(MsgCat.TOOL,
                    NamedTextColor.LIGHT_PURPLE, "WORLD SPAWN!", MM."<gray>Set to \{waypoint.getNameMM()}<gray> waypoint!",
                    SoundEvent.ENTITY_VILLAGER_WORK_CLERIC, Pitch.rng(1.5, 0.3));
        } else {
            player.playSound(SoundEvent.BLOCK_FUNGUS_PLACE, 0.5);
        }
    }

    @Override
    public boolean canSeeButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;
        CreativeInstance instance = player.getInstance();
        return instance.getWorldDef().ownership().isOwner(player);
    }

    @Override
    public Material getIconMaterial(boolean enabled) {
        return Material.RED_BED;
    }

    @Override
    public String getName() {
        return "Set World Spawn";
    }

    @Override
    public List<Component> getDescription() {
        Lore lore = new Lore();
        lore.wrap("<gray>Visitors will appear at this waypoint.");
        lore.addEmpty();
        lore.wrap(MM."<gray>You may also use <light_purple>/\{RespawnCommand.LABEL}<gray>.");
        return lore.asList();
    }
}
