package io.kloon.gameserver.modes.creative.menu.clipboard;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.commands.tools.ClipboardCommand;
import io.kloon.gameserver.modes.creative.menu.tools.ToolPickupButton;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.PlayerClipboard;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolsListener;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.menu.elems.ClipboardSlotButton;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ClipboardMenu extends ChestMenu {
    public static final String ICON = "\uD83D\uDCCB"; // 📋

    private final ChestMenu parent;
    private final ToolsListener tools;

    public ClipboardMenu(ChestMenu parent, ToolsListener tools) {
        super("Clipboard");
        this.parent = parent;
        this.tools = tools;
    }

    @Override
    protected void registerButtons() {
        for (int index = 0; index < PlayerClipboard.CLIPBOARD_ENTRIES; ++index) {
            int slot = 20 + index;
            reg(slot, new ClipboardSlotButton(index, slot));
        }

        reg().goBack(parent);

        CopyPasteTool copyPasteTool = (CopyPasteTool) tools.get(CreativeToolType.COPY_PASTE);
        reg(size.bottomCenter() + 1, new ToolPickupButton(copyPasteTool));
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<#FFBD26>\{ICON} <title>Clipboard";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{ClipboardCommand.LABEL}");
        lore.addEmpty();
        lore.wrap("<gray>Copy pieces of the world into your clipboard and paste them elsewhere or in other worlds.");
        lore.addEmpty();
        lore.add("<cta>Click to open!");

        return MenuStack.of(Material.HEAVY_CORE, name, lore);
    }
}
