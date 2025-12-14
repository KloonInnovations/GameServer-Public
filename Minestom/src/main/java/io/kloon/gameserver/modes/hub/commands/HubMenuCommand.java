package io.kloon.gameserver.modes.hub.commands;

import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.modes.hub.HubMode;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class HubMenuCommand extends Command {
    public static final String LABEL = "menu";

    public HubMenuCommand() {
        super(LABEL);
        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                player.sendPit(NamedTextColor.GREEN, "MENU!", MM."<gray>Enjoy this great item, with limitless possibilities!");
                player.playSound(SoundEvent.ENTITY_ITEM_PICKUP, 1.3);
                PlayerInventory inv = player.getInventory();
                if (inv.getItemStack(8).isAir()) {
                    inv.setItemStack(8, HubMode.MAIN_MENU_STACK);
                } else {
                    inv.addItemStack(HubMode.MAIN_MENU_STACK);
                }
            }
        });
    }
}
