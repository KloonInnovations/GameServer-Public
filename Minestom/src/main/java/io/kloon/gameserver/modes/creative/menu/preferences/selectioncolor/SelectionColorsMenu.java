package io.kloon.gameserver.modes.creative.menu.preferences.selectioncolor;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.playerdata.SelectionColors;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SelectionColorsMenu extends ChestMenu {
    public static final String ICON = "⃣"; // ⃣

    private final ChestMenu parent;

    public SelectionColorsMenu(ChestMenu parent) {
        super("Selection Outline Color", ChestSize.FOUR);
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        reg(11, new AutoSelectionColor(this));

        reg(13, new FieldSelectionColor(this, "No Selection", Material.RED_NETHER_BRICK_SLAB,
                SelectionColors::getNoSelection, (color, storage) -> storage.setNoSelection(color)));
        reg(14, new FieldSelectionColor(this, "One Corner", Material.RED_NETHER_BRICK_STAIRS,
                SelectionColors::getOneSelection, (color, storage) -> storage.setOneSelection(color)));
        reg(15, new FieldSelectionColor(this, "Full Selection", Material.RED_NETHER_BRICKS,
                SelectionColors::getFullSelection, (color, storage) -> storage.setFullSelection(color)));

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;
        SelectionColors selectionColors = player.getCreativeStorage().getSelectionColors();

        Component name = MM."<\{selectionColors.getFullSelectionHex()}>\{ICON} <title>Selection Outline Color";

        Lore lore = new Lore();
        lore.wrap("<gray>Set the highlight color your selection box to your tastes.");
        lore.addEmpty();
        lore.add(MM."<gray>No: <\{selectionColors.getNoSelectionHex()}><b>COLOR</b>");
        lore.add(MM."<gray>One: <\{selectionColors.getOneSelectionHex()}><b>COLOR</b>");
        lore.add(MM."<gray>Full: <\{selectionColors.getFullSelectionHex()}><b>COLOR</b>");
        lore.addEmpty();
        lore.add("<cta>Click to pick colors!");

        return MenuStack.of(Material.LEAD, name, lore);
    }
}
