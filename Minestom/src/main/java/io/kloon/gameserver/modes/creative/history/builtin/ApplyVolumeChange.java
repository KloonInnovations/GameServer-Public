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
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeCodec;

import java.io.IOException;

public class ApplyVolumeChange implements Change {
    private final BlockVolume before;
    private final BlockVolume after;

    public ApplyVolumeChange(BlockVolume before, BlockVolume after) {
        this.before = before;
        this.after = after;
    }

    public BlockVolume getBefore() {
        return before;
    }

    public BlockVolume getAfter() {
        return after;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.APPLY_VOLUME;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        ApplyBlockVolumeWork work = new ApplyBlockVolumeWork(ctx.instance(), before);
        return new WorkChangeResult(work);
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        ApplyBlockVolumeWork work = new ApplyBlockVolumeWork(ctx.instance(), after);
        return new WorkChangeResult(work);
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<ApplyVolumeChange> {
        @Override
        public void encode(ApplyVolumeChange change, MinecraftOutputStream out) throws IOException {
            out.write(change.before, BlockVolumeCodec.INSTANCE);
            out.write(change.after, BlockVolumeCodec.INSTANCE);
        }

        @Override
        public ApplyVolumeChange decode(MinecraftInputStream in) throws IOException {
            BlockVolume before = in.read(BlockVolumeCodec.INSTANCE);
            BlockVolume after = in.read(BlockVolumeCodec.INSTANCE);
            return new ApplyVolumeChange(before, after);
        }
    }
}
