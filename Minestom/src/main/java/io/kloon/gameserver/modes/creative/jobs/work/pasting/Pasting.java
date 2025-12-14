package io.kloon.gameserver.modes.creative.jobs.work.pasting;

import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipFlip;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipRotation;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

public record Pasting(
        BlockVolume volume,
        MaskLookup mask,
        Point start,
        Vec rotationPivot,
        ClipRotation rotation,
        ClipFlip flip,
        boolean ignorePasteAir,
        boolean ignoreMasks,
        boolean transformProperties
) {
}
