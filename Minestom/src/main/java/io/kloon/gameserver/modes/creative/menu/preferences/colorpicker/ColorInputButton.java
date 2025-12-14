package io.kloon.gameserver.modes.creative.menu.preferences.colorpicker;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.color.Color;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.function.BiConsumer;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ColorInputButton implements ChestButton {
    private final ChestMenu menuItsIn;
    private final BiConsumer<KloonPlayer, RGBLike> onPickColor;

    public ColorInputButton(ChestMenu menuItsIn, BiConsumer<KloonPlayer, RGBLike> onPickColor) {
        this.menuItsIn = menuItsIn;
        this.onPickColor = onPickColor;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        KloonPlayer player = (KloonPlayer) p;
        String[] inputLines = SignUX.inputLines("Hex = #FF00AA", "RGB = 255,0,170");
        SignUX.display(player, inputLines, inputs -> {
            Color color;
            try {
                color = parseColor(inputs[0]);
            } catch (Throwable t) {
                player.playSound(SoundEvent.ENTITY_ALLAY_HURT, 1.8f);
                player.sendPit(NamedTextColor.RED, "INVALID!", MM."<Couldn't read your color input!");
                menuItsIn.display(player);
                return;
            }

            onPickColor.accept(player, color);
        });
    }

    private static Color parseColor(String input) throws Exception {
        if (input.contains(",")) {
            String[] splits = input.split(",");
            if (splits.length != 3) throw new RuntimeException("Need 3 parts in color");
            int red = Integer.parseUnsignedInt(splits[0]);
            int green = Integer.parseUnsignedInt(splits[1]);
            int blue = Integer.parseUnsignedInt(splits[2]);
            return new Color(red, green, blue);
        } else {
            input = input.replace("#", "").trim();
            int colorInt = Integer.parseInt(input, 16);
            return new Color(colorInt);
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<rainbow>Pick Custom Color!!!!!";

        Lore lore = new Lore();
        lore.wrap("<gray>Enter a HEX or RGB value for your color.");
        lore.addEmpty();
        lore.add("<yellow>Click to select!");

        return MenuStack.of(Material.MANGROVE_SIGN, name, lore);
    }
}
