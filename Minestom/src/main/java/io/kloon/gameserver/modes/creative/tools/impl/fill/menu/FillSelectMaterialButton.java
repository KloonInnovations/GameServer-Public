package io.kloon.gameserver.modes.creative.tools.impl.fill.menu;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.impl.fill.FillTool;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.tools.menus.ToolPatternSelectionButton;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class FillSelectMaterialButton extends ToolPatternSelectionButton {
    private final FillTool tool;

    protected FillSelectMaterialButton(int slot, FillToolMenu menu) {
        super(menu, slot);
        this.tool = menu.getTool();
    }

    @Override
    public ItemStack renderButton(Player player) {
        FillTool.Settings settings = tool.getItemBound(itemRef.getItem());
        CreativePattern pattern = settings.getPattern();

        Component title = MM."<title>What to Fill With";

        Lore lore = new Lore();
        lore.add(ToolDataType.ITEM_BOUND.lore());

        if (pattern == null) {
            lore.wrap("<gray>Which block will fill your <selection><gray>.");
        } else {
            lore.add(MM."<gray>Fill \{pattern.getType().getPropertyName()}");
            lore.add(pattern.lore());
        }
        lore.addEmpty();

        lore.add("<gray>Ways to select:");
        lore.add(" <green>1. <gray>Click a block in");
        lore.add("    <gray>your <green>inventory<gray>.");
        lore.add(" <yellow>2. <gray>Sneak+click a block");
        lore.add("    <gray>in the <yellow>world<gray>.");
        lore.add(" <red>3. <gray>Click this button");
        lore.add("    <gray>to open a <red>menu<gray>.");
        lore.addEmpty();
        lore.add(getCallToAction(pattern));

        return settings.getIcon().name(title).lore(lore).glowing().build();
    }
}
