package io.kloon.gameserver.modes.creative.storage.playerdata;

import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.inventories.CreativeInventoryCodecV1;
import io.kloon.infra.mongo.storage.BufferedDocument;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InventoryStorage {
    private static final Logger LOG = LoggerFactory.getLogger(InventoryStorage.class);

    private final BufferedDocument document;

    private final CreativeInventoryCodecV1 codec;

    public InventoryStorage(BufferedDocument document, CreativePlayer player) {
        this.document = document;
        this.codec = new CreativeInventoryCodecV1(player);
    }

    @Nullable
    public List<ItemStack> getContents() throws Exception {
        return getItems(CONTENTS);
    }

    public void setContents(List<ItemStack> items) throws Exception {
        setItems(CONTENTS, items);
        document.putInt(CODEC_VERSION, codec.getVersion());
    }

    public int getHeldSlot() {
        return document.getInt(HELD_SLOT, 0);
    }

    public void setHeldSlot(int slot) {
        document.putInt(HELD_SLOT, slot);
    }

    @Nullable
    private List<ItemStack> getItems(String key) throws Exception {
        byte[] bytes = document.getBinary(key);
        if (bytes == null) return null;
        return codec.decode(new MinecraftInputStream(bytes));
    }

    private void setItems(String key, List<ItemStack> items) throws Exception {
        byte[] bytes = MinecraftOutputStream.toBytes(items, codec);
        document.putBinary(key, bytes);
    }

    public int getStoredCodecVersion() {
        int defaultVersion = document.containsKey(CONTENTS) ? 1 : codec.getVersion();
        return document.getInt(CODEC_VERSION, defaultVersion);
    }

    public boolean hasProperCodecVersion() {
        return codec.getVersion() == getStoredCodecVersion();
    }

    private static final String CONTENTS = "contents";
    private static final String HELD_SLOT = "held_slot";
    private static final String CODEC_VERSION = "codec_version";
}
