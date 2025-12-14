package io.kloon.gameserver.modes.creative.tools.impl.stack.menu;

import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.ItemBoundNumberButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.impl.stack.StackTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class StackToolMenu extends CreativeToolMenu<StackTool> {
    public static final NumberInput<StackTool.Settings> STACKS = NumberInput.consumerInt(
            Material.TRIDENT, NamedTextColor.GOLD,
            "Stacks (Multiples)",
            MM_WRAP."<gray>How many times to stack your selection.",
            1, 1, 15,
            StackTool.Settings::getStacks, StackTool.Settings::setStacks);

    public static final NumberInput<StackTool.Settings> OFFSET = NumberInput.consumerInt(
            Material.FEATHER, NamedTextColor.GREEN,
            "Offset (Blocks)",
            MM_WRAP."<gray>How many blocks are skipped in-between each stacks.",
            1, 1, 64,
            StackTool.Settings::getOffset, StackTool.Settings::setOffset);

    public static final ToolToggle<StackTool.Preferences> AUTO_SELECT = new ToolToggle<>(
            Material.LEAD,
            "Auto-Select",
            MM_WRAP."<gray>If enabled, your selection will adjust to the stacked construct after using the tool.",
            StackTool.Preferences::isAdjustSelection, StackTool.Preferences::setAdjustSelection);

    public StackToolMenu(StackTool tool, ItemRef itemRef) {
        super(tool, itemRef);
    }

    @Override
    protected void registerButtons() {
        reg(11, slot -> new ItemBoundNumberButton<>(slot, this, STACKS));
        reg(15, slot -> new ItemBoundNumberButton<>(slot, this, OFFSET));

        regPreferenceToggle(31, AUTO_SELECT);

        reg().toolCommands(this);
    }
}
