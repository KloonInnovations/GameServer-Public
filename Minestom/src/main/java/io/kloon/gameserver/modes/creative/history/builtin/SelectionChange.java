package io.kloon.gameserver.modes.creative.history.builtin;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.InstantResult;
import io.kloon.gameserver.modes.creative.selection.CuboidSelection;
import io.kloon.gameserver.modes.creative.storage.datainworld.SelectionCuboidStorage;

import java.io.IOException;

public class SelectionChange implements Change {
    private final SelectionCuboidStorage before;
    private final SelectionCuboidStorage after;

    public SelectionChange(SelectionCuboidStorage before, SelectionCuboidStorage after) {
        this.before = before;
        this.after = after;
    }

    public SelectionChange(SelectionCuboidStorage before, CreativePlayer playerAfter) {
        this.before = before;
        this.after = playerAfter.getSelection().toStorage();
    }

    @Override
    public ChangeType getType() {
        return ChangeType.ADJUST_SELECTION;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        adjustSelection(ctx.player(), before);
        return new InstantResult();
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        adjustSelection(ctx.player(), after);
        return new InstantResult();
    }

    private void adjustSelection(CreativePlayer player, SelectionCuboidStorage newSelectionStorage) {
        player.getSelection().remove();
        CuboidSelection newSelection = newSelectionStorage.createSelection(player);
        player.setSelection(newSelection);
    }

    public static final Codec CODEC = new Codec();
    public static final class Codec implements MinecraftCodec<SelectionChange> {
        @Override
        public void encode(SelectionChange obj, MinecraftOutputStream out) throws IOException {
            out.write(obj.before, SelectionCuboidStorage.CODEC);
            out.write(obj.after, SelectionCuboidStorage.CODEC);
        }

        @Override
        public SelectionChange decode(MinecraftInputStream in) throws IOException {
            return new SelectionChange(
                    in.read(SelectionCuboidStorage.CODEC),
                    in.read(SelectionCuboidStorage.CODEC)
            );
        }
    }
}
