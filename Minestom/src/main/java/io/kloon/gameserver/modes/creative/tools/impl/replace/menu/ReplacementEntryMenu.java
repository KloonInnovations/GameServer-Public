package io.kloon.gameserver.modes.creative.tools.impl.replace.menu;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.builtin.CancelButton;
import io.kloon.gameserver.chestmenus.builtin.ConfirmPaddingButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.impl.replace.menu.entry.*;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementConfig;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementEntry;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ReplacementEntryMenu extends ChestMenu {
    private final ReplaceToolMenu parent;
    private final @Nullable ReplacementEntry editing;

    private Block replacing;
    private CreativePattern whatToReplaceWith;
    private boolean replaceOnExactState = false;

    public ReplacementEntryMenu(ReplaceToolMenu parent, @Nullable ReplacementEntry editing) {
        super("Replacement Entry", ChestSize.FOUR);
        this.parent = parent;
        this.editing = editing;

        if (editing != null) {
            this.replacing = editing.replacing();
            this.whatToReplaceWith = editing.whatToReplaceWith();
            this.replaceOnExactState = editing.replaceOnExactState();
        }
    }

    public ReplaceToolMenu getParent() {
        return parent;
    }

    public ReplacementConfig getReplacementConfig() {
        ReplacementConfig replacementConfig = parent.getReplacementConfig().copy();
        if (editing != null) {
            replacementConfig.remove(editing.replacing(), editing.replaceOnExactState());
        }
        return replacementConfig;
    }

    public void setReplacementConfig(CreativePlayer player, ReplacementConfig config) {
        parent.getTool().editItemBound(player, parent.getItemRef(), settings -> {
            settings.setReplacement(config);
        });
    }

    public boolean isEditing() {
        return editing != null;
    }

    public @Nullable ReplacementEntry getEditing() {
        return editing;
    }

    @Nullable
    public Block getReplacing() {
        return replacing;
    }

    public void setReplacing(Block replacing) {
        this.replacing = replacing;
    }

    @Nullable
    public CreativePattern getWhatToReplaceWith() {
        return whatToReplaceWith;
    }

    public void setWhatToReplaceWith(CreativePattern whatToReplaceWith) {
        this.whatToReplaceWith = whatToReplaceWith;
    }

    public boolean isReplaceOnExactState() {
        return replaceOnExactState;
    }

    public void setReplaceOnExactState(boolean replaceOnExactState) {
        this.replaceOnExactState = replaceOnExactState;
    }

    @Override
    protected void registerButtons() {
        reg(11, new ReplacingButton(this));
        reg(12, slot -> new ToggleReplaceExactButton(this, slot));

        reg(15, new WhatToReplaceWithButton(this));

        if (editing != null) {
            reg(size.bottomCenter() - 9, new DeleteReplacementEntryButton(this));
        }

        reg(size.bottomCenter() - 2, new CancelButton(parent));
        reg(size.bottomCenter() - 1, new ConfirmPaddingButton());
        reg(size.bottomCenter(), new ConfirmReplacementEntryButton(this));
        reg(size.bottomCenter() + 1, new ConfirmPaddingButton());
    }

    @Override
    public ItemStack renderButton(Player player) {
        Lore lore = new Lore();
        if (editing == null) {
            Component name = MM."<title>Create Entry";
            lore.wrap("<gray>Add a new block to be replaced.");
            lore.addEmpty();
            lore.add("<cta>Click to create!");
            return MenuStack.of(Material.LIME_CONCRETE, name, lore);
        }

        Component name = MM."<title>\{BlockFmt.getName(editing.replacing())}";

        lore.add(MM."<dark_gray>Block ➜ \{editing.whatToReplaceWith().getTypeName()}");
        lore.addEmpty();

        TinkeredBlock tinkered = new TinkeredBlock(editing.replacing(), editing.replaceOnExactState());
        lore.add(MM."<gray>Replacing: \{tinkered.getNameMM()}");
        if (editing.replaceOnExactState()) {
            lore.add(MM."<dark_gray>Exact match only!");
        }
        lore.addEmpty();
        lore.add(MM."<gray>With: \{whatToReplaceWith.labelMM()}");
        if (!(whatToReplaceWith instanceof SingleBlockPattern)) {
            lore.add(editing.whatToReplaceWith().lore());
        }

        lore.addEmpty();
        lore.add("<cta>Click to edit!");

        Material icon = editing.replacing().registry().material();
        if (icon == null) {
            icon = Material.BEDROCK;
        }
        return MenuStack.of(icon, name, lore);
    }
}
