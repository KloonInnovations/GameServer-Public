package io.kloon.gameserver.modes.creative.history.builtin;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.WorkChangeResult;
import io.kloon.gameserver.modes.creative.jobs.work.ApplyBlockVolumeWork;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.SetCuboidWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeCodec;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.instance.block.Block;

import java.io.IOException;

public class SetCuboidChange implements Change {
    private final BlockVolume before;
    private final Block after;

    public SetCuboidChange(BlockVolume before, Block after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.SET_CUBOID;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        ApplyBlockVolumeWork work = new ApplyBlockVolumeWork(ctx.instance(), before);
        return new WorkChangeResult(work);
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        BoundingBox cuboid = toCuboid();
        SetCuboidWork work = new SetCuboidWork(ctx.instance(), cuboid, after);
        return new WorkChangeResult(work);
    }

    private BoundingBox toCuboid() {
        return before.toCuboid();
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<SetCuboidChange> {
        @Override
        public void encode(SetCuboidChange change, MinecraftOutputStream out) throws IOException {
            out.write(change.before, BlockVolumeCodec.INSTANCE);
            out.write(change.after, DumbPalette.BLOCK_CODEC_FULL);
        }

        @Override
        public SetCuboidChange decode(MinecraftInputStream in) throws IOException {
            BlockVolume before = in.read(BlockVolumeCodec.INSTANCE);
            Block after = in.read(DumbPalette.BLOCK_CODEC_FULL);
            return new SetCuboidChange(before, after);
        }
    }
}
