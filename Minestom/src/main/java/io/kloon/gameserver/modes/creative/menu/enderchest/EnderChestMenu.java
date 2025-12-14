package io.kloon.gameserver.modes.creative.menu.enderchest;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.listing.MenuList;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.enderchest.EnderChestCommand;
import io.kloon.gameserver.modes.creative.commands.enderchest.EnderChestSaveCommand;
import io.kloon.gameserver.modes.creative.menu.enderchest.item.EnderChestItemMenu;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestItem;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class EnderChestMenu extends ChestMenu {
    public static final String ICON = "\uD83D\uDCDA"; // 📚

    private final ChestMenu parent;
    private final CreativePlayer player;

    private final MenuList<EnderChestItem> menuList;

    public EnderChestMenu(ChestMenu parent, CreativePlayer player) {
        super("Ender Chest");
        this.parent = parent;
        this.player = player;

        this.menuList = new MenuList<>(this, ChestLayouts.INSIDE, item -> new EnderChestItemMenu(this, item));
    }

    public CreativePlayer getPlayer() {
        return player;
    }

    @Override
    protected void registerButtons() {
        List<EnderChestItem> items = new ArrayList<>(player.getEnderChest().getItems());
        items.sort(Comparator.comparingLong(EnderChestItem::getCreationTimestamp));

        if (items.isEmpty()) {
            reg(size.middleCenter(), new EnderChestInfoButton(player));
        } else {
            menuList.distribute(items, this::reg);
            reg(size.bottomCenter() + 3, new EnderChestInfoButton(player));
        }

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<#F051FF>\{ICON} <title>Ender Chest";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{EnderChestCommand.LABEL_SHORT}");
        lore.addEmpty();

        lore.wrap("<gray>Store tools, blocks and patterns long-term. Shared across worlds.");

        lore.addEmpty();
        lore.add("<cta>Click to open!");

        return MenuStack.of(Material.ENDER_CHEST, name, lore);
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        event.setCancelled(true);

        CreativePlayer player = (CreativePlayer) event.getPlayer();

        ItemStack clickedItem = event.getClickedItem();
        if (clickedItem.isAir()) {
            player.playSound(SoundEvent.ENTITY_ARMADILLO_HURT, Pitch.rng(1.8, 0.2));
            return;
        }

        boolean saved = EnderChestSaveCommand.saveItem(player, clickedItem);
        if (saved) {
            reload().display(player);
        }
    }
}
