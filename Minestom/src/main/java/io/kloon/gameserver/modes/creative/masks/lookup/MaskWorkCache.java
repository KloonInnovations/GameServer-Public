package io.kloon.gameserver.modes.creative.masks.lookup;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.Nullable;

public record MaskWorkCache(
        @Nullable BoundingBox selectionBb,
        Vec lookDir,
        Proximity3dCache proximity3d
) {
    public static MaskWorkCache create(CreativePlayer player) {
        BoundingBox selectionBb = null;
        if (player.getSelection() instanceof TwoCuboidSelection two) {
            selectionBb = two.getCuboid();
        }

        Vec lookDir = player.getLookVec();

        return new MaskWorkCache(selectionBb, lookDir, new Proximity3dCache());
    }
}
