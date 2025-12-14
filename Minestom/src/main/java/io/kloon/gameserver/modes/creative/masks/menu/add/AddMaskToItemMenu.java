package io.kloon.gameserver.modes.creative.masks.menu.add;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskTypes;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.menu.masks.MasksSelectionMenu;
import io.kloon.gameserver.util.RandUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class AddMaskToItemMenu extends ChestMenu {
    private final EditMaskItemMenu parent;
    private final MaskType<?> duplicateType;

    public AddMaskToItemMenu(EditMaskItemMenu parent, @Nullable MaskType<?> duplicateType) {
        super("Select Mask");
        this.parent = parent;
        this.duplicateType = duplicateType;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        if (duplicateType != null && click.isRightClick()) {
            new AddMaskToItemButton(parent, duplicateType).clickButton(player, click);
            return;
        }

        super.clickButton(player, click);
    }

    @Override
    protected void registerButtons() {
        ChestLayouts.INSIDE.distribute(MaskTypes.getList(), (slot, mask) -> {
            reg(slot, new AddMaskToItemButton(parent, mask));
        });

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Add Mask to Item";

        Lore lore = new Lore();
        lore.wrap("<gray>Select and add another mask to this item.");
        lore.addEmpty();
        if (duplicateType == null) {
            lore.add("<cta>Click to select another mask!");
        } else {
            lore.add("<rcta>Click to copy mask type!");
            lore.add("<lcta>Click to select another mask!");
        }

        String icon = RandUtil.getRandom(MasksSelectionMenu.MASK_HEADS);
        return MenuStack.ofHead(icon).name(name).lore(lore).build();
    }
}
