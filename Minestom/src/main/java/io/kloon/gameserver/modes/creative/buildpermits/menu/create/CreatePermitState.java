package io.kloon.gameserver.modes.creative.buildpermits.menu.create;

import io.kloon.gameserver.modes.creative.buildpermits.duration.PermitDuration;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CreatePermitState {
    private UUID recipientUuid;
    private PermitDuration duration;

    public UUID getRecipientUuid() {
        return recipientUuid;
    }

    public void setRecipientUuid(UUID recipientUuid) {
        this.recipientUuid = recipientUuid;
    }

    @Nullable
    public KloonPlayer getRecipient(Instance instance) {
        if (recipientUuid == null) return null;
        return (KloonPlayer) instance.getPlayerByUuid(recipientUuid);
    }

    @Nullable
    public PermitDuration getDuration() {
        return duration;
    }

    public void setDuration(PermitDuration duration) {
        this.duration = duration;
    }
}
