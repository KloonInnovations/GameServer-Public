package io.kloon.gameserver.modes.creative.menu.preferences.selectioncolor;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.colorpicker.ColorPickerMenu;
import io.kloon.gameserver.modes.creative.storage.playerdata.SelectionColors;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class AutoSelectionColor implements ChestButton {
    private final ChestMenu parent;

    public AutoSelectionColor(ChestMenu parent) {
        this.parent = parent;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        new ColorPickerMenu(parent, "Selection Highlight (Auto)", (kp, main) -> {
            CreativePlayer player = (CreativePlayer) kp;

            SelectionColors generated = SelectionColors.generate(main.asHSV());
            String noSelectionHex = generated.getNoSelectionHex();

            player.getCreativeStorage().getSelectionColors().replaceAll(generated);

            player.playSound(SoundEvent.ENTITY_AXOLOTL_SPLASH, 0.9f);
            player.sendPit(main, "NEW COLORS", MM."<\{noSelectionHex}>Picked a new set of selection colors!");
            parent.display(p);
        }).display(p);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;
        SelectionColors selectionColors = player.getCreativeStorage().getSelectionColors();

        Component name = MM."<title>Auto-Selection";

        Lore lore = new Lore();
        lore.wrap("<gray>Pick 1 full color and it generates the other 2.");
        lore.addEmpty();
        lore.add(MM."<gray>No: <\{selectionColors.getNoSelectionHex()}><b>COLOR</b>");
        lore.add(MM."<gray>One: <\{selectionColors.getOneSelectionHex()}><b>COLOR</b>");
        lore.add(MM."<gray>Full: <\{selectionColors.getFullSelectionHex()}><b>COLOR</b>");
        lore.addEmpty();
        lore.add("<cta>Click to pick!");

        return MenuStack.of(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, name, lore);
    }
}
