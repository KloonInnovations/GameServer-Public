package io.kloon.gameserver.modes.creative.storage.datainworld;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.CuboidSelection;
import io.kloon.gameserver.modes.creative.selection.NoCuboidSelection;
import io.kloon.gameserver.modes.creative.selection.OneCuboidSelection;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.storage.datainworld.minestom.StorageVec;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public record SelectionCuboidStorage(
        @Nullable StorageVec pos1,
        @Nullable StorageVec pos2
) {
    private static final Logger LOG = LoggerFactory.getLogger(SelectionCuboidStorage.class);

    public static SelectionCuboidStorage fromVec(Vec pos1, Vec pos2) {
        return new SelectionCuboidStorage(new StorageVec(pos1), new StorageVec(pos2));
    }

    public CuboidSelection createSelection(CreativePlayer player) {
        if (pos1 == null && pos2 == null) {
            return new NoCuboidSelection(player);
        } else if (pos1 != null && pos2 != null) {
            return TwoCuboidSelection.spawn(player, pos1.toVec(), pos2.toVec());
        }
        StorageVec nonNull = pos1 == null ? pos2 : pos1;
        return OneCuboidSelection.spawn(player, nonNull.toVec());
    }

    public static final Codec CODEC = new Codec();
    public static final class Codec implements MinecraftCodec<SelectionCuboidStorage> {
        @Override
        public void encode(SelectionCuboidStorage obj, MinecraftOutputStream out) throws IOException {
            out.writeOptional(obj.pos1, StorageVec.CODEC);
            out.writeOptional(obj.pos2, StorageVec.CODEC);
        }

        @Override
        public SelectionCuboidStorage decode(MinecraftInputStream in) throws IOException {
            return new SelectionCuboidStorage(
                    in.readOptional(StorageVec.CODEC),
                    in.readOptional(StorageVec.CODEC)
            );
        }
    }
}
