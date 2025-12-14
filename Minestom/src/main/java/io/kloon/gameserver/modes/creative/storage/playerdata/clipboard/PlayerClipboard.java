package io.kloon.gameserver.modes.creative.storage.playerdata.clipboard;

import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.stored.CompressedClipboardEntry;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.stored.StoredClipboardEntry;
import io.kloon.infra.mongo.accounts.KloonAccount;
import io.kloon.infra.mongo.storage.BufferedDocument;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerClipboard {
    private final KloonAccount account;
    private final BufferedDocument document;

    private final List<WorldClip> clips = new ArrayList<>();
    private final Map<ObjectId, WorldClip> clipsByEntry = new HashMap<>();

    private BlocksJob copyJob = null;

    public static final int CLIPBOARD_ENTRIES = 5;

    public PlayerClipboard(KloonAccount account, BufferedDocument document) {
        this.account = account;
        this.document = document;

        for (int i = 0; i < CLIPBOARD_ENTRIES; ++i) {
            StoredClipboardEntry storedEntry = document.getObject(getEntryKey(i), CompressedClipboardEntry.CODEC);
            WorldClip clip = storedEntry == null ? null : storedEntry.toClip();

            clips.add(clip);
            if (clip != null) {
                clipsByEntry.put(clip.id(), clip);
            }
        }

        if (clips.size() < CLIPBOARD_ENTRIES) {
            int missing = CLIPBOARD_ENTRIES - clips.size();
            clips.addAll(Collections.nCopies(missing, null));
        }
    }

    // returns -1 if none found
    public int getFirstUnusedIndex() {
        return clips.indexOf(null);
    }

    public int getClipIndex(WorldClip clip) {
        if (clip == null) return -1;
        return clips.indexOf(clip);
    }

    @Nullable
    public WorldClip getClip(int index) {
        if (index < 0 || index >= clips.size()) return null;
        return clips.get(index);
    }

    @Nullable
    public WorldClip getClip(ObjectId id) {
        return clipsByEntry.get(id);
    }

    public void setClip(int index, @NotNull WorldClip clip) {
        clips.set(index, clip);
        clipsByEntry.put(clip.id(), clip);

        CompressedClipboardEntry stored = clip.toStored();
        document.putObject(getEntryKey(index), stored, CompressedClipboardEntry.CODEC);
    }

    public void remove(WorldClip entry) {
        WorldClip removed = clipsByEntry.remove(entry.id());
        if (removed == null) return;
        int index = clips.indexOf(removed);
        if (index >= 0) {
            clips.set(index, null);
            document.remove(getEntryKey(index));
        }
    }

    @Nullable
    public BlocksJob getCopyJob() {
        if (copyJob == null || copyJob.isEnded()) {
            return null;
        }
        return copyJob;
    }

    public void setCopyJob(BlocksJob copyJob) {
        this.copyJob = copyJob;
    }

    private String getEntryKey(int index) {
        return String.valueOf(index);
    }

    private static final String ENTRIES = "entries";
}
