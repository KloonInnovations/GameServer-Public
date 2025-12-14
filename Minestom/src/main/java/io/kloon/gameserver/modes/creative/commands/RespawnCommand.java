package io.kloon.gameserver.modes.creative.commands;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class RespawnCommand extends Command {
    public static final String LABEL = "respawn";

    public RespawnCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativeInstance instance = player.getInstance();
                WaypointStorage waypoint = instance.getWorldStorage().getWaypoints().getWorldSpawn();
                if (waypoint == null) {
                    Pos worldSpawn = instance.getWorldSpawn();
                    player.teleport(worldSpawn);

                    player.msg().send(MsgCat.POSITION, NamedTextColor.DARK_PURPLE, "RESPAWNED!", MM."<gray>On default world spawn!");
                } else {
                    Pos worldSpawn = waypoint.getPosition();
                    player.teleport(worldSpawn);

                    player.msg().send(MsgCat.POSITION, NamedTextColor.DARK_PURPLE, "RESPAWNED!", MM."<gray>On spawn waypoint \{waypoint.getNameMM()}<gray>!");
                }
                player.playSound(SoundEvent.ENTITY_ENDERMAN_TELEPORT, Pitch.rng(0.5, 0.24));
            }
        });
    }
}
