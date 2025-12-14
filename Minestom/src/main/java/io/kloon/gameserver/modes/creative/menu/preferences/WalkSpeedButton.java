package io.kloon.gameserver.modes.creative.menu.preferences;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.preferences.FlySpeedCommand;
import io.kloon.gameserver.modes.creative.commands.preferences.WalkSpeedCommand;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.commands.preferences.FlySpeedCommand.toFmtSpeed;

public class WalkSpeedButton implements ChestButton {
    public static final String ICON = "\uD83D\uDEB6"; // 🚶

    private final int slot;

    public WalkSpeedButton(int slot) {
        this.slot = slot;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        if (!isWalkSpeedDefault(player) && click.isRightClick()) {
            WalkSpeedCommand.setWalkSpeed(player, WalkSpeedCommand.DEFAULT_WALK_SPEED);
            ChestMenuInv.rerenderButton(slot, player);
            return;
        }

        float min = WalkSpeedCommand.MIN_SPEED_DISPLAY;
        float max = WalkSpeedCommand.MAX_SPEED_DISPLAY;

        String[] displayLines = SignUX.inputLines("Walk Speed", min, max, NumberFmt.NO_DECIMAL);
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(min, max).build(player, speedInput -> {
            float speed = FlySpeedCommand.fromFmtSpeed(speedInput.floatValue());
            WalkSpeedCommand.setWalkSpeed(player, speed);
        }));
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<gold>\{ICON} <title>Edit Walk Speed";

        List<Component> lore = new ArrayList<>();
        lore.add(ToolDataType.PLAYER_BOUND.getLoreSubtitle());
        lore.add(MM."<cmd>\{WalkSpeedCommand.LABEL}");
        lore.add(Component.empty());

        lore.addAll(MM_WRAP."<gray>A mindful walk today can prevent a frantic sprint tomorrow... unless your homework is due.");
        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<dark_gray>This is your BASE walk speed.");
        lore.add(Component.empty());

        float baseSpeed = player.getCreativeStorage().getWalkSpeed();
        lore.add(MM."<gray>Your speed: <gold>\{toFmtSpeed(baseSpeed)}");
        if (WalkSpeedCommand.DEFAULT_WALK_SPEED == baseSpeed) {
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to edit!");
        } else {
            lore.addAll(MM_WRAP."<dark_gray>Default = \{toFmtSpeed(WalkSpeedCommand.DEFAULT_WALK_SPEED)}");
            if (baseSpeed <= WalkSpeedCommand.DEFAULT_WALK_SPEED / 2) {
                lore.add(Component.empty());
                lore.add(MM."<dark_gray>I like turtles. <dark_green>\uD83D\uDC22 \uD83D\uDC22 \uD83D\uDC22"); // 🐢
            }

            lore.add(Component.empty());
            lore.add(MM."<rcta>Click to reset!");
            lore.add(MM."<lcta>Click to edit!");
        }

        return MenuStack.of(Material.LEATHER_BOOTS).name(name).lore(lore).build();
    }

    private boolean isWalkSpeedDefault(Player player) {
        return player.getAttributeValue(Attribute.MOVEMENT_SPEED) == WalkSpeedCommand.DEFAULT_WALK_SPEED;
    }
}
