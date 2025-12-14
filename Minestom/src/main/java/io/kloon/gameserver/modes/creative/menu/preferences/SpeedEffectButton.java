package io.kloon.gameserver.modes.creative.menu.preferences;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.preferences.SpeedEffectCommand;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class SpeedEffectButton implements ChestButton {
    public static final String ICON = "\uD83C\uDFC3"; // 🏃

    private final int slot;

    public SpeedEffectButton(int slot) {
        this.slot = slot;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        int amplifier = player.getEffectLevel(PotionEffect.SPEED);
        amplifier += click.isRightClick() ? -1 : 1;
        if (amplifier > SpeedEffectCommand.MAX) {
            amplifier = -1;
        } else if (amplifier <= -2) {
            amplifier = SpeedEffectCommand.MAX;
        }

        SpeedEffectCommand.setSpeedEffect(player, amplifier);
        ChestMenuInv.rerenderButton(slot, player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<blue>\{ICON} <title>Speed Potion Effect";

        List<Component> lore = new ArrayList<>();
        lore.add(ToolDataType.PLAYER_BOUND.getLoreSubtitle());
        lore.add(MM."<cmd>\{SpeedEffectCommand.LABEL} [amplifier]");
        lore.add(Component.empty());

        lore.addAll(MM_WRAP."<gray>Multiplies your base walk speed. Useful for parkours.");
        lore.add(Component.empty());

        int amplifier = player.getEffectLevel(PotionEffect.SPEED);
        if (amplifier < 0) {
            lore.add(MM."<gray>You don't have Speed!");
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to +1 amplifier!");
        } else {
            lore.add(MM."<gray>Amplifier: <aqua>\{amplifier}");
            if (amplifier == 0) {
                lore.add(MM."<dark_gray>0 means Speed I");
            }
            lore.add(Component.empty());

            lore.add(MM."<rcta>Click to -1 amplifier!");
            lore.add(MM."<lcta>Click to +1 amplifier!");
        }

        return MenuStack.of(Material.DIAMOND_BOOTS).name(name).lore(lore).build();
    }
}
