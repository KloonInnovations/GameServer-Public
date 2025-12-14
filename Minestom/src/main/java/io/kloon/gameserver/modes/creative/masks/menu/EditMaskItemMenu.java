package io.kloon.gameserver.modes.creative.masks.menu;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.add.AddMaskToItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.common.ToggleUnionButton;
import io.kloon.gameserver.modes.creative.masks.menu.material.MaskItemColorButton;
import io.kloon.gameserver.modes.creative.masks.menu.material.MaskItemTypeMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class EditMaskItemMenu extends ChestMenu {
    private final CreativePlayer player;
    private final MaskItem maskItem;
    private final ItemRef itemRef;

    public EditMaskItemMenu(CreativePlayer player, MaskItem maskItem, ItemRef itemRef) {
        super(maskItem.getMasks().size() > 1 ? "Multi-Mask Item" : "Mask Item");
        this.player = player;
        this.maskItem = maskItem;
        this.itemRef = itemRef;
    }

    public CreativePlayer getPlayer() {
        return player;
    }

    public MaskItem getMaskItem() {
        return maskItem;
    }

    public ItemRef getItemRef() {
        return itemRef;
    }

    public <Data> void updateMaskAndDisplay(CreativePlayer player, MaskWithData<Data> mask) {
        MaskItem edited = maskItem.withEditedMask(mask);
        ItemStack stack = edited.renderItem();
        boolean updated = itemRef.setIfDidntChange(stack);
        if (!updated) {
            player.closeInventory();
            return;
        }

        EditMaskItemMenu editedMenu = new EditMaskItemMenu(player, edited, itemRef);
        ChestMenu maskMenu = mask.type().createMaskMenu(editedMenu, mask);
        if (maskItem.getMasks().size() == 1 || maskMenu == null) {
            editedMenu.display(player);
        } else {
            maskMenu.display(player);
        }
    }

    public void updateMaskAndDisplay(CreativePlayer player, MaskItem edited) {
        ItemStack stack = edited.renderItem();
        boolean updated = itemRef.setIfDidntChange(stack);
        if (!updated) {
            player.closeInventory();
            return;
        }

        EditMaskItemMenu editedMenu = new EditMaskItemMenu(player, edited, itemRef);
        editedMenu.display(player);
    }

    @Override
    protected void registerButtons() {
        List<MaskWithData<?>> masks = maskItem.getMasks();
        if (masks.isEmpty()) {
            reg(size.middleCenter(), MenuStack.of(Material.RED_BANNER)
                    .name(MM."<red>No masks?")
                    .lore(MM_WRAP."<gray>This mask item doesn't hold any masks. It's kind of invalid tbh.")
                    .buildButton());
            return;
        }

        if (masks.size() == 1) {
            reg(size.middleCenter(), MenuStack.of(Material.RED_BANNER)
                    .name(MM."<red>No mask menu!")
                    .lore(MM_WRAP."<gray>The single mask in this item doesn't have an edit menu. Awkward!")
                    .buildButton());
            return;
        }

        int slot = 11;
        for (int i = 0; i < masks.size(); ++i) {
            MaskWithData mask = masks.get(i);
            ChestMenu menu = mask.type().createMaskMenu(this, mask);
            reg(slot, menu);
            ++slot;
        }
        if (masks.size() < MaskItem.MASKS_LIMIT) {
            reg(slot, new AddMaskToItemMenu(this, null));
        }

        reg(31, new ToggleUnionButton(this));
        reg(size.last(), new MaskItemTypeMenu(this));
        reg(size.last() - 1, new MaskItemColorButton(this));
    }

    @Override
    public @Nullable ChestMenu getRedirectOnDisplay(Player player) {
        if (maskItem.getMasks().size() == 1) {
            MaskWithData mask = maskItem.getMasks().getFirst();
            return mask.type().createMaskMenu(this, mask);
        }
        return null;
    }
}
