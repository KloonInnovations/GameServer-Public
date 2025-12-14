package io.kloon.gameserver.modes.creative.storage.datainworld;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.History;
import io.kloon.gameserver.modes.creative.storage.datainworld.minestom.StoragePos;
import io.kloon.gameserver.modes.creative.storage.datainworld.util.CompressedHistory;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class PlayerWorldStorage {
    private StoragePos position;
    private boolean flying;
    private SelectionCuboidStorage selectionStorage;
    private long lastVisit;
    private CompressedHistory history;

    @Nullable
    public Pos getPosition() {
        return position == null ? null : position.toPos();
    }

    public void setPosition(Pos position) {
        this.position = new StoragePos(position);
    }

    public SelectionCuboidStorage getSelectionStorage() {
        return selectionStorage == null ? new SelectionCuboidStorage(null, null) : selectionStorage;
    }

    public void setSelectionStorage(SelectionCuboidStorage storage) {
        this.selectionStorage = storage;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public void setLastVisit(long lastVisit) {
        this.lastVisit = lastVisit;
    }

    public long getLastVisit() {
        return lastVisit;
    }

    @Nullable
    public CompressedHistory getCompressedHistory() {
        return history;
    }

    public History getHistory(CreativePlayer player) {
        return CompressedHistory.decode(player, history);
    }

    public void setHistory(History history) throws IOException {
        this.history = CompressedHistory.encode(history);
    }

    public void clearHistory() {
        this.history = null;
    }
}
