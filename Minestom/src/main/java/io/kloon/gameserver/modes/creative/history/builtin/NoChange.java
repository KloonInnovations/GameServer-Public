package io.kloon.gameserver.modes.creative.history.builtin;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.InstantResult;

import java.io.IOException;

// TODO: Surely this is an abuse of "Change", especially with children classes
public class NoChange implements Change {
    @Override
    public final ChangeType getType() {
        return ChangeType.NO_CHANGE;
    }

    @Override
    public final ChangeResult undo(ChangeContext ctx) {
        return new InstantResult();
    }

    @Override
    public final ChangeResult redo(ChangeContext ctx) {
        return new InstantResult();
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<NoChange> {
        @Override
        public void encode(NoChange obj, MinecraftOutputStream out) throws IOException {

        }

        @Override
        public NoChange decode(MinecraftInputStream in) throws IOException {
            return new NoChange();
        }
    }
}
