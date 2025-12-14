package io.kloon.gameserver.modes.creative.buildpermits.duration;

import io.kloon.gameserver.chestmenus.util.Lore;

public record InfinitePermit() implements PermitDuration {
    @Override
    public long toMs() {
        return Long.MAX_VALUE;
    }

    @Override
    public String formattedMM() {
        return "<light_purple>Endless!</light_purple>";
    }

    @Override
    public Lore lore() {
        Lore lore = new Lore();
        lore.wrap("<gray>This permit won't expire.");
        lore.addEmpty();
        lore.add("<red>Careful! That's a very long time!");
        return lore;
    }
}
