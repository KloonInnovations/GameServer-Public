package io.kloon.gameserver.modes.creative.menu.preferences;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.preferences.FlySpeedCommand;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.commands.preferences.FlySpeedCommand.*;

public class FlySpeedButton implements ChestButton {
    public static final String ICON = "\uD83E\uDEB6"; // 🪶

    private final int slot;

    public FlySpeedButton(int slot) {
        this.slot = slot;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        if (!isFlySpeedDefault(player) && click.isRightClick()) {
            FlySpeedCommand.setFlySpeed(player, DEFAULT_FLY_SPEED);
            ChestMenuInv.rerenderButton(slot, player);
            return;
        }

        float min = MIN_SPEED_DISPLAY;
        float max = MAX_SPEED_DISPLAY;

        String[] displayLines = SignUX.inputLines("Enter Speed", min, max, NumberFmt.ONE_DECIMAL);
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(min, max).build(player, flySpeedInput -> {
            float speed = fromFmtSpeed(flySpeedInput.floatValue());
            FlySpeedCommand.setFlySpeed(player, speed);
        }));
    }

    private boolean isFlySpeedDefault(Player player) {
        return player.getFlyingSpeed() == DEFAULT_FLY_SPEED;
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<yellow>\{ICON} <title>Edit Fly Speed";

        List<Component> lore = new ArrayList<>();
        lore.add(ToolDataType.PLAYER_BOUND.getLoreSubtitle());
        lore.add(MM."<cmd>\{LABEL}");
        lore.add(Component.empty());

        lore.addAll(MM_WRAP."<gray>Too fast? Too slow?");
        lore.add(Component.empty());

        float speed = player.getFlyingSpeed();
        lore.add(MM."<gray>Your speed: <yellow>\{toFmtSpeed(speed)}");
        if (DEFAULT_FLY_SPEED == speed) {
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to edit!");
        } else {
            lore.addAll(MM_WRAP."<dark_gray>Default = \{toFmtSpeed(DEFAULT_FLY_SPEED)}");
            lore.add(Component.empty());
            lore.add(MM."<rcta>Click to reset!");
            lore.add(MM."<lcta>Click to edit!");
        }

        return MenuStack.of(Material.FEATHER)
                .name(name)
                .lore(lore)
                .build();
    }
}
