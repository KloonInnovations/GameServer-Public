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
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.color.Color;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class FieldSelectionColor implements ChestButton {
    private final ChestMenu parent;

    private final String fieldLabel;
    private final Material icon;
    private final Function<SelectionColors, Color> get;
    private final BiConsumer<Color, SelectionColors> set;

    public FieldSelectionColor(
            ChestMenu parent,
            String fieldLabel, Material icon,
            Function<SelectionColors, Color> get,
            BiConsumer<Color, SelectionColors> set) {
        this.parent = parent;
        this.fieldLabel = fieldLabel;
        this.icon = icon;
        this.get = get;
        this.set = set;
    }


    @Override
    public void clickButton(Player p, ButtonClick click) {
        new ColorPickerMenu(parent, STR."Highlight (\{fieldLabel})", (kp, color) -> {
            CreativePlayer player = (CreativePlayer) kp;
            SelectionColors selectionColors = player.getCreativeStorage().getSelectionColors();
            set.accept(new Color(color), selectionColors);

            player.playSound(SoundEvent.ENTITY_AXOLOTL_SPLASH, 1.3f);
            player.sendPit(color, "NEW COLOR", MM."<gray>Picked a new \{fieldLabel.toLowerCase()} highlight color!");
            parent.display(p);
        }).display(p);
    }

    @Override
    public ItemStack renderButton(Player p) {
        Component name = MM."<title>\{fieldLabel}";

        Lore lore = new Lore();
        lore.wrap("<gray>Pick the color for this specific selection state.");
        lore.addEmpty();

        CreativePlayer player = (CreativePlayer) p;
        Color color = get.apply(player.getCreativeStorage().getSelectionColors());
        String hex = TextColor.color(color).asHexString();
        lore.add(MM."<gray>Current: <\{hex}><b>COLOR</b>");
        lore.addEmpty();

        lore.add("<cta>Click to pick!");

        return MenuStack.of(icon, name, lore);
    }
}
