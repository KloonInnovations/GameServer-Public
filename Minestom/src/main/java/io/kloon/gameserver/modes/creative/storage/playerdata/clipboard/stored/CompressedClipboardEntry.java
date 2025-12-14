package io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.stored;

import com.github.luben.zstd.Zstd;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeCodec;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClip;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClipDetails;
import org.bson.types.ObjectId;

public record CompressedClipboardEntry(
        ObjectId id,
        WorldClipDetails details,
        int volumeSize,
        byte[] compressedVolume
) implements StoredClipboardEntry {
    public long timestamp() {
        return id.getTimestamp() * 1000L;
    }

    @Override
    public WorldClip toClip() {
        byte[] decompressed = Zstd.decompress(compressedVolume, volumeSize);
        BlockVolume volume = MinecraftInputStream.fromBytesSneaky(decompressed, BlockVolumeCodec.INSTANCE);
        return new WorldClip(id, details, volume);
    }
}
