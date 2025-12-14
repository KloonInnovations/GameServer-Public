package io.kloon.gameserver.modes.creative.tools.impl.replace.menu.entry;

import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.tools.impl.replace.menu.ReplacementEntryMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.util.List;

public class ToggleReplaceExactButton extends ToggleButton {
    private final ReplacementEntryMenu menu;
    private final int slot;

    public ToggleReplaceExactButton(ReplacementEntryMenu menu, int slot) {
        this.menu = menu;
        this.slot = slot;
    }

    @Override
    public boolean isEnabled(Player player) {
        return menu.isReplaceOnExactState();
    }

    @Override
    public void setEnabled(Player player, boolean enabled) {
        menu.setReplaceOnExactState(enabled);
    }

    @Override
    public void onValueChange(Player player, boolean newValue) {
        ChestMenuInv.rerenderButton(slot, player, menu);
    }

    @Override
    public Material getIconMaterial(boolean enabled) {
        return enabled ? Material.PURPLE_DYE : Material.GRAY_DYE;
    }

    @Override
    public String getName() {
        return "Replace Exact Block";
    }

    @Override
    public boolean canSeeButton(Player player) {
        Block replacing = menu.getReplacing();
        return replacing != null && !replacing.propertyOptions().isEmpty();
    }

    @Override
    public List<Component> getDescription() {
        Lore lore = new Lore();
        lore.wrap("<gray>Whether to replace any block of this type, or only this specific tinkered version.");
        lore.addEmpty();
        lore.wrap("<gray>Example: <i>only</i> OPEN acacia doors, or <i>any</i> acacia doors.");
        return lore.asList();
    }
}
