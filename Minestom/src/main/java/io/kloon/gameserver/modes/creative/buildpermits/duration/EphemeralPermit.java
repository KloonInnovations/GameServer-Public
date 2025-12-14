package io.kloon.gameserver.modes.creative.buildpermits.duration;

import io.kloon.gameserver.chestmenus.util.Lore;

public record EphemeralPermit() implements PermitDuration {
    @Override
    public long toMs() {
        return -1;
    }

    @Override
    public String formattedMM() {
        return "<light_purple><i>Ephemeral</i></light_purple>";
    }

    @Override
    public Lore lore() {
        return new Lore().wrap("<gray>Expires as soon as you disconnect from the world.");
    }
}
