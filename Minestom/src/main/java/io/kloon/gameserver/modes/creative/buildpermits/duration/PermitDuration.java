package io.kloon.gameserver.modes.creative.buildpermits.duration;

import io.kloon.gameserver.chestmenus.util.Lore;

import java.time.Duration;

public sealed interface PermitDuration permits EphemeralPermit, InfinitePermit, TimedPermit {
    long toMs();

    String formattedMM();

    Lore lore();

    static PermitDuration fromMs(long ms) {
        if (ms <= 0) return new EphemeralPermit();
        if (ms == Long.MAX_VALUE) return new InfinitePermit();
        return new TimedPermit(Duration.ofMillis(ms));
    }
}
