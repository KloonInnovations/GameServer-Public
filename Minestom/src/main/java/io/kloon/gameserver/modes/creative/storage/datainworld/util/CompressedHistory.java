package io.kloon.gameserver.modes.creative.storage.datainworld.util;

import com.github.luben.zstd.Zstd;
import io.kloon.gameserver.MiniMessageTemplate;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.History;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CompressedHistory {
    private static final Logger LOG = LoggerFactory.getLogger(CompressedHistory.class);

    private final byte codecVersion;
    private final int originalSize;
    private final byte[] compressed;

    public CompressedHistory() {
        this((byte) 0, 0, null);
    }

    private CompressedHistory(byte codecVersion, int originalSize, byte[] compressed) {
        this.codecVersion = codecVersion;
        this.originalSize = originalSize;
        this.compressed = compressed;
    }

    public static CompressedHistory encode(History history) throws IOException {
        byte[] encoded = MinecraftOutputStream.toBytes(history, History.CODEC);
        byte[] compressed = Zstd.compress(encoded);
        byte codecVersion = (byte) History.Codec.VERSION;
        return new CompressedHistory(codecVersion, encoded.length, compressed);
    }

    public static History decode(@Nullable CompressedHistory cp) {
        return decode(null, cp);
    }

    public static History decode(@Nullable CreativePlayer player, @Nullable CompressedHistory cp) {
        try {
            if (cp == null) {
                return History.createEmpty();
            }

            if (cp.codecVersion != History.Codec.VERSION) {
                messageLater(player, "<gray>Your undo history was reset because our storage systems changed!");
                return History.createEmpty();
            }

            byte[] decompressed = Zstd.decompress(cp.compressed, cp.originalSize);
            return History.CODEC.decode(new MinecraftInputStream(decompressed));
        } catch (Throwable t) {
            messageLater(player, "<gray>Your undo history was reset because there was an error loading it!");
            LOG.error("Error decoding history", t);
            return History.createEmpty();
        }
    }

    private static void messageLater(@Nullable CreativePlayer player, String mm) {
        if (player == null) return;
        player.scheduler().scheduleTask(() -> {
            player.sendPit(NamedTextColor.RED, "OOPS!", MiniMessageTemplate.toComponent(mm));
        }, TaskSchedule.tick(20), TaskSchedule.stop());
    }
}
