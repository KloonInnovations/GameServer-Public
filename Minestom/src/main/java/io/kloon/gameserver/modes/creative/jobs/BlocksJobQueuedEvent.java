package io.kloon.gameserver.modes.creative.jobs;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import org.jetbrains.annotations.NotNull;

public class BlocksJobQueuedEvent implements PlayerInstanceEvent {
    private final CreativePlayer player;
    private final BlocksJob job;

    public BlocksJobQueuedEvent(CreativePlayer player, BlocksJob job) {
        this.player = player;
        this.job = job;
    }

    @Override
    public @NotNull CreativeInstance getInstance() {
        return player.getInstance();
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public BlocksJob getJob() {
        return job;
    }
}
