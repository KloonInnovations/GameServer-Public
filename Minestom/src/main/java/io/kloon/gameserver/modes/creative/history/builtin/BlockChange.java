package io.kloon.gameserver.modes.creative.history.builtin;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.InstantResult;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import io.kloon.gameserver.modes.creative.storage.datainworld.minestom.StorageBlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.io.IOException;

public class BlockChange implements Change {
    private final StorageBlockVec blockPos;
    private final Block before;
    private final Block after;

    public BlockChange(Point blockPos, Block before, Block after) {
        this(new StorageBlockVec(blockPos), before, after);
    }

    public BlockChange(StorageBlockVec blockPos, Block before, Block after) {
        this.blockPos = blockPos;
        this.before = before;
        this.after = after;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.BLOCK_CHANGE;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        ctx.instance().setBlock(blockPos.toBlockVec(), before);
        return new InstantResult();
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        ctx.instance().setBlock(blockPos.toBlockVec(), after);
        return new InstantResult();
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<BlockChange> {
        @Override
        public void encode(BlockChange change, MinecraftOutputStream out) throws IOException {
            out.write(change.blockPos, StorageBlockVec.CODEC);
            out.write(change.before, DumbPalette.BLOCK_CODEC_FULL);
            out.write(change.after, DumbPalette.BLOCK_CODEC_FULL);
        }

        @Override
        public BlockChange decode(MinecraftInputStream in) throws IOException {
            return new BlockChange(
                    in.read(StorageBlockVec.CODEC),
                    in.read(DumbPalette.BLOCK_CODEC_FULL),
                    in.read(DumbPalette.BLOCK_CODEC_FULL)
            );
        }
    }
}
