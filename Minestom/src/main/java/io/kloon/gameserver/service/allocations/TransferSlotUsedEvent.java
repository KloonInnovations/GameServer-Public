package io.kloon.gameserver.service.allocations;

import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import org.jetbrains.annotations.NotNull;

public class TransferSlotUsedEvent implements PlayerInstanceEvent {
    private final KloonPlayer player;
    private final AllocatedSlot slot;

    public TransferSlotUsedEvent(KloonPlayer player, AllocatedSlot slot) {
        this.player = player;
        this.slot = slot;
    }

    @Override
    public @NotNull KloonPlayer getPlayer() {
        return player;
    }

    public AllocatedSlot getSlot() {
        return slot;
    }
}
