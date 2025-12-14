package io.kloon.gameserver.modes.creative.storage.enderchest;

import com.google.common.collect.Maps;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.util.cooldowns.impl.TickCooldown;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class EnderChestStorage {
    private static final Logger LOG = LoggerFactory.getLogger(EnderChestStorage.class);

    private final CreativePlayer player;
    private final EnderChestRepo repo;
    private final Map<ObjectId, EnderChestItem> items;
    private Set<ItemStack> itemStacks;

    private final TickCooldown actionCooldown = new TickCooldown(2);

    public static final int ITEMS_LIMIT = 128;

    public EnderChestStorage(CreativePlayer player, List<EnderChestItem> items) {
        this.player = player;
        this.repo = player.getCreative().getEnderChestRepo();
        this.items = new HashMap<>(Maps.uniqueIndex(items, EnderChestItem::getId));
        this.itemStacks = items.stream().map(EnderChestItem::getItemStack).collect(Collectors.toSet());
    }

    public Collection<EnderChestItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    // returns true if can continue
    public boolean checkActionCooldown() {
        if (!actionCooldown.cooldownIfPossible()) {
            player.sendPit(NamedTextColor.RED, "OOPS!", MM."<gray>Ender Chest action on cooldown!");
            return false;
        }
        return true;
    }

    public boolean hasItemStack(ItemStack itemStack) {
        return itemStacks.contains(EnderChestItem.sanitizeStack(itemStack));
    }

    public void save(EnderChestItem item) {
        itemStacks.add(item.getItemStack());
        EnderChestItem existing = items.put(item.getId(), item);
        if (existing == null) {
            repo.insert(item).exceptionally(this::handleError);
        } else {
            repo.update(item).exceptionally(this::handleError);
        }
    }

    public void delete(EnderChestItem item) {
        EnderChestItem removed = items.remove(item.getId());
        if (removed == null) {
            return;
        }
        this.itemStacks = items.values().stream().map(EnderChestItem::getItemStack).collect(Collectors.toSet());
        repo.delete(item.getId()).exceptionally(this::handleError);
    }

    private <T> T handleError(Throwable error) {
        player.sendPit(NamedTextColor.DARK_RED, "ERROR!", MM."<gray>Error saving ender chest to database!");
        LOG.error("Error saving ender chest to database", error);
        return null;
    }
}
