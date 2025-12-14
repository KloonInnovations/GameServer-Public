package io.kloon.gameserver.modes.creative.masks.menu.editmask;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.builtin.GoBackButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.add.AddMaskToItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.material.MaskItemColorButton;
import io.kloon.gameserver.modes.creative.masks.menu.material.MaskItemTypeMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class EditMaskMenu<Data> extends ChestMenu {
    protected final EditMaskItemMenu parent;
    protected final MaskWithData<Data> mask;

    public EditMaskMenu(EditMaskItemMenu parent, MaskWithData<Data> mask) {
        super(STR."\{mask.type().getName()} Mask", ChestSize.SIX);
        this.parent = parent;
        this.mask = mask;
    }

    public EditMaskItemMenu getParent() {
        return parent;
    }

    public MaskWithData<Data> getMask() {
        return mask;
    }

    public Data getData() {
        return mask.data();
    }

    public void updateMaskAndDisplay(CreativePlayer player, MaskWithData<Data> mask) {
        parent.updateMaskAndDisplay(player, mask);
    }

    @Override
    protected void registerButtons() {
        if (parent.getMaskItem().getMasks().size() > 1) {
            reg(size.bottomCenter(), new GoBackButton(parent).withReloadOnClick(true));
            reg(size.bottomCenter() + 1, new RemoveMaskFromItemButton(parent, mask));
        } else {
            reg(40, new AddMaskToItemMenu(parent, mask.type()));

            reg(size.last(), new MaskItemTypeMenu(parent));
            reg(size.last() - 1, new MaskItemColorButton(parent));
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        MaskType<Data> maskType = mask.type();

        Component name = MM."\{maskType.getNameMM()} Mask";

        Lore lore = new Lore();
        lore.add(maskType.getLore(mask.data(), mask.negated()));
        lore.addEmpty();
        lore.add("<cta>Click to edit!");

        return maskType.getIcon().name(name).lore(lore).build();
    }
}
