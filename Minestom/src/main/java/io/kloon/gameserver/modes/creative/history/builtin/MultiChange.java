package io.kloon.gameserver.modes.creative.history.builtin;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.ChangeResultList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiChange implements Change {
    private final List<Change> changes;

    public MultiChange(List<Change> changes) {
        this.changes = changes;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.MULTI;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        List<ChangeResult> results = new ArrayList<>(changes.size());
        for (Change change : changes) {
            ChangeResult result = change.undo(ctx);
            results.add(result);
        }
        return new ChangeResultList(results);
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        List<ChangeResult> results = new ArrayList<>(changes.size());
        for (Change change : changes) {
            ChangeResult result = change.redo(ctx);
            results.add(result);
        }
        return new ChangeResultList(results);
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<MultiChange> {
        @Override
        public void encode(MultiChange multi, MinecraftOutputStream out) throws IOException {
            List<Change> list = multi.changes;
            out.writeVarInt(list.size());
            for (Change change : list) {
                ChangeType changeType = change.getType();
                MinecraftCodec codec = change.getType().getCodec();
                out.writeVarInt(changeType.getDbKey());
                out.write(change, codec);
            }
        }

        @Override
        public MultiChange decode(MinecraftInputStream in) throws IOException {
            int count = in.readVarInt();
            List<Change> list = new ArrayList<>(count);
            for (int i = 0; i < count; ++i) {
                int typeId = in.readVarInt();
                ChangeType changeType = ChangeType.byDbKey(typeId);
                MinecraftCodec<? extends Change> codec = changeType.getCodec();
                Change change = in.read(codec);
                list.add(change);
            }
            return new MultiChange(list);
        }
    }
}
