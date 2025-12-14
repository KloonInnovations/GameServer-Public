package io.kloon.gameserver.modes.creative.tasks;

import io.kloon.gameserver.minestom.scheduler.Repeat;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import io.kloon.gameserver.util.RandUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.world.DimensionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class TeleportPlayersWhoAreTooFar implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(TeleportPlayersWhoAreTooFar.class);

    private final CreativeInstance instance;

    public TeleportPlayersWhoAreTooFar(CreativeInstance instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        Pos worldCenter = instance.getWorldSpawn();
        instance.streamPlayers().forEach(player -> {
            try {
                Pos position = player.getPosition();
                if (isTooFar(position)) {
                    player.teleport(worldCenter);
                    List<Component> messages = Arrays.asList(
                            MM."<gray>Can't go that far! That's crazy dangerous!",
                            MM."<gray>Can't go that far! That's where the fun is!",
                            MM."<gray>Can't go that far! There's nothing that way!",
                            MM."<gray>Can't go that far! It's not allowed!",
                            MM."<gray>Can't go that far! What if you got lost?"
                    );
                    player.sendPit(NamedTextColor.LIGHT_PURPLE, "WOAH THERE!", RandUtil.getRandom(messages));
                    double base = ThreadLocalRandom.current().nextDouble(0.3);
                    Repeat.n(player.scheduler(), 5, 2, t -> {
                        player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_BANJO, Pitch.base(2.0 - base - t * 0.25));
                    });
                }
            } catch (Throwable t) {
                LOG.error(STR."Error checking if \{player.getName()} is too far", t);
            }
        });
    }

    private boolean isTooFar(Point position) {
        DimensionType dimension = instance.getCachedDimensionType();
        if (position.y() < dimension.minY() - 128) {
            return true;
        }
        if (position.y() > dimension.maxY() + 128) {
            return true;
        }
        CreativeWorldStorage worldStorage = instance.getWorldStorage();
        int chunkRadius = worldStorage.getWorldSize().getChunksRadius() + 4;
        int blocksRadius = chunkRadius * 16;
        Pos center = worldStorage.getWorldCenter().toPos();

        double distX = Math.abs(position.x() - center.x());
        double distZ = Math.abs(position.z() - center.z());
        return distX > blocksRadius || distZ > blocksRadius;
    }
}
