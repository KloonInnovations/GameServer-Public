package io.kloon.gameserver.modes.creative.history;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.history.EndOfHistoryResult;
import io.kloon.gameserver.modes.creative.history.results.history.HistoryChangedResult;
import io.kloon.gameserver.modes.creative.history.results.history.HistoryEditResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class History {
    private static final Logger LOG = LoggerFactory.getLogger(History.class);

    private final List<ChangeRecord> past;
    private final List<ChangeRecord> future;

    private final List<OngoingChange> present = new ArrayList<>();

    public static final int RECORDS_LIMIT = 25;

    public History(List<ChangeRecord> past, List<ChangeRecord> future) {
        this.past = past;
        this.future = future;
    }

    public List<ChangeRecord> getPast() {
        return Collections.unmodifiableList(past);
    }

    public List<OngoingChange> getOngoing() {
        return Collections.unmodifiableList(present);
    }

    public List<ChangeRecord> getFuture() {
        return Collections.unmodifiableList(future);
    }

    public static History createEmpty() {
        return new History(new ArrayList<>(), new ArrayList<>());
    }

    public void add(OngoingChange ongoing) {
        add(ongoing, true);
    }

    public void add(OngoingChange ongoing, boolean clearFuture) {
        present.add(ongoing);

        if (clearFuture) {
            future.clear();
        }

        ongoing.future().whenComplete((change, t) -> {
            present.remove(ongoing);
            if (t != null) {
                LOG.error("Ignored change from history because of error", t);
                return;
            }
            past.add(new ChangeRecord(ongoing.startTimestamp(), System.currentTimeMillis(), ongoing.author(), ongoing.meta(), change));
            applyLimits(past);
        });
    }

    public void add(ChangeRecord record) {
        add(record, true);
    }

    public void add(ChangeRecord record, boolean clearFuture) {
        past.add(record);
        applyLimits(past);
        if (clearFuture) {
            future.clear();
        }
    }

    public void addFuture(ChangeRecord record) {
        future.add(record);
        applyLimits(future);
    }

    public boolean hasOngoingChanges() {
        return !present.isEmpty();
    }

    public HistoryEditResult undo(CreativePlayer player) {
        if (past.isEmpty()) {
            return new EndOfHistoryResult();
        }

        ChangeRecord latestPast = past.removeLast();
        future.add(latestPast);
        applyLimits(future);

        ChangeContext ctx = new ChangeContext(latestPast, player, player.getInstance());

        ChangeResult undoResult = latestPast.change().undo(ctx);
        return new HistoryChangedResult(latestPast, undoResult);
    }

    public HistoryEditResult redo(CreativePlayer player) {
        if (future.isEmpty()) {
            return new EndOfHistoryResult();
        }

        ChangeRecord latestFuture = future.removeLast();
        ChangeContext ctx = new ChangeContext(latestFuture, player, player.getInstance());

        ChangeResult redoResult = latestFuture.change().redo(ctx);
        return new HistoryChangedResult(latestFuture, redoResult);
    }

    private void applyLimits(List<ChangeRecord> records) {
        if (records.size() < RECORDS_LIMIT) {
            return;
        }
        int over = records.size() - RECORDS_LIMIT;
        records.subList(0, over).clear();
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<History> {
        public static final int VERSION = 4;

        @Override
        public void encode(History history, MinecraftOutputStream out) throws IOException {
            encodeRecordList(history.past, out);
            encodeRecordList(history.future, out);
        }

        @Override
        public History decode(MinecraftInputStream in) throws IOException {
            List<ChangeRecord> past = decodeRecordList(in);
            List<ChangeRecord> future = decodeRecordList(in);
            return new History(past, future);
        }

        private void encodeRecordList(List<ChangeRecord> records, MinecraftOutputStream out) throws IOException {
            out.writeVarInt(records.size());
            for (ChangeRecord record : records) {
                try {
                    ChangeRecord.CODEC.encode(record, out);
                } catch (Throwable t) {
                    throw new RuntimeException(STR."Error encoding \{record.change().getType()}", t);
                }
            }
        }

        private List<ChangeRecord> decodeRecordList(MinecraftInputStream in) throws IOException {
            int count = in.readVarInt();
            List<ChangeRecord> records = new ArrayList<>(count);
            for (int i = 0; i < count; ++i) {
                ChangeRecord record = ChangeRecord.CODEC.decode(in);
                records.add(record);
            }
            return records;
        }
    }
}
