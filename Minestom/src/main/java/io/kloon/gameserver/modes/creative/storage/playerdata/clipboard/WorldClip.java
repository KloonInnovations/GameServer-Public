package io.kloon.gameserver.modes.creative.storage.playerdata.clipboard;

import com.github.luben.zstd.Zstd;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeCodec;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.stored.CompressedClipboardEntry;
import org.bson.types.ObjectId;

public record WorldClip(
        ObjectId id,
        WorldClipDetails details,
        BlockVolume volume
) {
    public CompressedClipboardEntry toStored() {
        byte[] encoded = MinecraftOutputStream.toBytesSneaky(volume, BlockVolumeCodec.INSTANCE);
        byte[] compressed = Zstd.compress(encoded);
        return new CompressedClipboardEntry(id, details, encoded.length, compressed);
    }
}
