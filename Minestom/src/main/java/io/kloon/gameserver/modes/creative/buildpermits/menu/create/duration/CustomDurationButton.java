package io.kloon.gameserver.modes.creative.buildpermits.menu.create.duration;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.buildpermits.duration.TimedPermit;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitMenu;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.input.DurationInput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CustomDurationButton implements ChestButton {
    private final CreatePermitMenu createMenu;
    private final PermitDurationMenu durationMenu;

    public CustomDurationButton(CreatePermitMenu createMenu, PermitDurationMenu durationMenu) {
        this.createMenu = createMenu;
        this.durationMenu = durationMenu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        KloonPlayer player = (KloonPlayer) p;

        String[] inputLines = SignUX.inputLines("Enter duration", "2m30s, 24h, 14d...");
        SignUX.display(player, inputLines, inputs -> {
            String input = inputs[0];
            try {
                long durationMs = DurationInput.parse(input);
                if (durationMs <= 0 || durationMs == Long.MAX_VALUE) {
                    throw new RuntimeException("Out of bounds");
                }
                TimedPermit duration = new TimedPermit(Duration.ofMillis(durationMs));
                createMenu.getState().setDuration(duration);

                createMenu.reload().display(player);
            } catch (Throwable t) {
                player.playSound(SoundEvent.ENTITY_SHULKER_DEATH, 1.4);
                player.sendPit(NamedTextColor.RED, "CAN'T PARSE!", MM."<gray>Not sure what duration you entered!");
                durationMenu.display(player);
            }
        });
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Custom Duration";

        Lore lore = new Lore();
        lore.wrap("<gray>Input a custom duration for the build permit.");
        lore.addEmpty();
        lore.add("<cta>Click to edit!");

        return MenuStack.of(Material.CHERRY_HANGING_SIGN, name, lore);
    }
}
