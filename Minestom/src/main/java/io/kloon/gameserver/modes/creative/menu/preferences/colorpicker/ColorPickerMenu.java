package io.kloon.gameserver.modes.creative.menu.preferences.colorpicker;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.RGBHeads;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ColorPickerMenu extends ChestMenu {
    private final ChestMenu parent;
    private final BiConsumer<KloonPlayer, RGBLike> onPickColor;

    public ColorPickerMenu(ChestMenu parent, String label, BiConsumer<KloonPlayer, RGBLike> onPickColor) {
        super("Color Picker");
        this.parent = parent;
        this.onPickColor = onPickColor;

        setTitleFunction(p -> MM."\{label} ➜ Color Picker");
    }

    @Override
    protected void registerButtons() {
        List<? extends ChestButton> buttons = HARDCODED_COLORS.stream().map(color -> {
            return new ChestButton() {
                public void clickButton(Player player, ButtonClick click) {
                    if (player instanceof KloonPlayer kp) {
                        onPickColor.accept(kp, color);
                    }
                }

                public ItemStack renderButton(Player player) {
                    String hex = color.asHexString();
                    Component name = MM."<\{hex}>\{InputFmt.CLICK} Click to pick this!";
                    return RGBHeads.getClosestHead(color).name(name).build();
                }
            };
        }).toList();
        ChestLayouts.INSIDE.distribute(buttons, this::reg);

        reg(size.bottomCenter() - 9, new ColorInputButton(this, onPickColor));

        reg().goBack(parent);
    }

    private static final List<TextColor> HARDCODED_COLORS = Arrays.asList(
            TextColor.color(255, 0, 0),
            TextColor.color(255, 63, 0),
            TextColor.color(255, 127, 0),
            TextColor.color(255, 191, 0),
            TextColor.color(255, 255, 0),
            TextColor.color(191, 255, 0),
            TextColor.color(127, 255, 0),
            TextColor.color(0, 255, 0),
            TextColor.color(0, 255, 63),
            TextColor.color(0, 255, 127),
            TextColor.color(0, 255, 191),
            TextColor.color(0, 255, 255),
            TextColor.color(0, 191, 255),
            TextColor.color(0, 127, 255),
            TextColor.color(0, 63, 255),
            TextColor.color(0, 0, 255),
            TextColor.color(63, 0, 255),
            TextColor.color(127, 0, 255),
            TextColor.color(191, 0, 255),
            TextColor.color(255, 0, 255),
            TextColor.color(255, 0, 127)
    );
}
