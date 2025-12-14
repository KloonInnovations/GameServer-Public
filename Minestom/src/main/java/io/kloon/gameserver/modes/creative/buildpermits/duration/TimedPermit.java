package io.kloon.gameserver.modes.creative.buildpermits.duration;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.util.formatting.TimeFmt;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.LocalDateTime;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public record TimedPermit(Duration duration) implements PermitDuration {
    @Override
    public long toMs() {
        return duration.toMillis();
    }

    @Override
    public String formattedMM() {
        return DurationFormatUtils.formatDurationWords(duration.toMillis(), true, true);
    }

    @Override
    public Lore lore() {
        LocalDateTime expiry = LocalDateTime.now().plus(duration);
        String expiryFmt = TimeFmt.date(expiry);
        return new Lore().wrap(MM."<gray>The recipient will hold the permit until <green>\{expiryFmt}<gray>.");
    }

    public long expiryMs(long issued) {
        return issued + duration.toMillis();
    }
}
