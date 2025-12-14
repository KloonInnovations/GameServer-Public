package io.kloon.gameserver.modes.creative.storage.enderchest;

import io.kloon.gameserver.minestom.io.MinecraftEncoder;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.storage.inventories.items.CreativeItemDecoderV1;
import io.kloon.gameserver.modes.creative.storage.inventories.items.CreativeItemEncoderV1;
import io.kloon.infra.mongo.storage.BufferedDocument;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import org.bson.Document;
import org.bson.types.ObjectId;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class EnderChestItem {
    private static final MinecraftEncoder<ItemStack> ITEM_ENCODER = CreativeItemEncoderV1.INSTANCE;

    private final BufferedDocument document;

    private final ObjectId id;
    private ItemStack itemStack;

    public EnderChestItem(BufferedDocument document, CreativeItemDecoderV1 itemDecoder) {
        this.document = document;

        this.id = document.getObjectId(ID);

        byte[] itemBytes = document.getBinary(ITEM);
        this.itemStack = MinecraftInputStream.fromBytesSneaky(itemBytes, itemDecoder);
    }

    public EnderChestItem(ObjectId id, ObjectId playerId, ItemStack stack) {
        this.id = id;

        this.itemStack = sanitizeStack(stack);

        this.document = new BufferedDocument(new Document());
        document.putObjectId(ID, id);
        document.putObjectId(PLAYER, playerId);

        byte[] itemBytes = MinecraftOutputStream.toBytesSneaky(itemStack, ITEM_ENCODER);
        document.putBinary(ITEM, itemBytes);
    }

    public ObjectId getId() {
        return id;
    }

    public ObjectId getPlayerId() {
        return document.getObjectId(PLAYER);
    }

    public long getCreationTimestamp() {
        return id.getTimestamp() * 1000L;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Component getName() {
        Component name = itemStack.get(DataComponents.CUSTOM_NAME);
        if (name != null) {
            return name;
        }
        return MM."<white>\{BlockFmt.getName(itemStack.material())}";
    }

    public void setItemStack(ItemStack itemStack) {
        byte[] bytes = MinecraftOutputStream.toBytesSneaky(itemStack, ITEM_ENCODER);
        document.putBinary(ITEM, bytes);
        this.itemStack = itemStack;
    }

    public BufferedDocument getDocument() {
        return document;
    }

    public static ItemStack sanitizeStack(ItemStack stack) {
        return stack.withAmount(1);
    }

    public static final String ID = "_id";
    public static final String PLAYER = "player";
    private static final String ITEM = "item";
}
