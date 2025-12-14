package io.kloon.gameserver.modes.creative.tools.impl.replace.menu.entry;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.states.ButtonState;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.tools.impl.replace.menu.ReplacementEntryMenu;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementConfig;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementEntry;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ConfirmReplacementEntryButton implements ChestButton {
    private final ReplacementEntryMenu menu;

    public ConfirmReplacementEntryButton(ReplacementEntryMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        ButtonState state = getState();
        if (!state.isPositive()) {
            state.sendChatMessage(player);
            return;
        }

        ReplacementEntry editing = menu.getEditing();

        Block replacing = menu.getReplacing();
        CreativePattern whatToReplaceWith = menu.getWhatToReplaceWith();
        boolean exact = menu.isReplaceOnExactState();

        ReplacementConfig config = menu.getReplacementConfig();
        if (editing != null) {
            config.remove(editing.replacing(), editing.replaceOnExactState());
        }
        config.put(replacing, whatToReplaceWith, exact);

        menu.setReplacementConfig(player, config);

        menu.getParent().reload().display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Confirm Entry";

        Block replacing = menu.getReplacing();
        CreativePattern whatToReplaceWith = menu.getWhatToReplaceWith();
        boolean exact = menu.isReplaceOnExactState();

        Lore lore = new Lore();
        if (replacing == null) {
            lore.add(MM."<gray>Replacing: <red>Not set!");
        } else {
            TinkeredBlock tinkered = new TinkeredBlock(replacing, exact);
            lore.add(MM."<gray>Replacing: \{tinkered.getNameMM()}");
        }

        if (whatToReplaceWith == null) {
            lore.add(MM."<gray>With: <red>No set!");
        } else {
            lore.add(MM."<gray>With: \{whatToReplaceWith.labelMM()}");
        }

        if (exact) {
            lore.addEmpty();
            lore.add(MM."<dark_gray>Exact match only!");
        }

        ButtonState state = getState();

        lore.addEmpty();
        lore.add(state.getCallToAction(player));

        Material icon = replacing == null ? null : replacing.registry().material();
        icon = icon == null ? Material.MAGMA_CREAM : icon;
        if (!state.isPositive()) {
            icon = Material.RED_TERRACOTTA;
        }

        return MenuStack.of(icon, name, lore);
    }

    private ButtonState getState() {
        Block replacing = menu.getReplacing();
        CreativePattern whatToReplaceWith = menu.getWhatToReplaceWith();
        if (replacing == null || whatToReplaceWith == null) {
            return MISSING_FIELDS;
        }

        ReplacementConfig replacementConfig = menu.getReplacementConfig();
        if (replacementConfig.get(replacing) != null) {
            return EXISTING_REPLACING;
        }

        if (replacementConfig.size() + 1 > ReplacementConfig.MAX_ENTRIES) {
            return REACHED_MAX_ENTRIES;
        }

        return menu.isEditing() ? CAN_EDIT : CAN_CREATE;
    }

    private static final ButtonState CAN_CREATE = new ButtonState("<cta>Click to create entry!").withPositive();
    private static final ButtonState CAN_EDIT = new ButtonState("<cta>Click to edit entry!").withPositive();
    private static final ButtonState MISSING_FIELDS = new ButtonState("<!cta>Missing some choices!");
    private static final ButtonState EXISTING_REPLACING = new ButtonState("<!cta>Duplicate replacing!").withChat("<red>There's an existing entry in the replacement config for this block!");
    private static final ButtonState REACHED_MAX_ENTRIES = new ButtonState("<!cta>Reached max entries!");

}
