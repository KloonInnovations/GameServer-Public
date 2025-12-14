package io.kloon.gameserver.modes.creative.commands;

import io.kloon.bigbackend.client.admin.TransferClient;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.modes.ModeType;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class BackToHubCommand extends Command {
    public static final String LABEL = "hub";

    public BackToHubCommand() {
        super(LABEL, "lobby", "l");

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                sendToHub(player);
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        });
    }

    public static void sendToHub(KloonPlayer player) {
        TransferClient transfers = Kgs.getBackend().getTransfers();
        player.sendMessage(MM."<gray>Sending you to the hub...");
        player.allocateAndTransfer(p -> transfers.allocateModeTransfer(ModeType.HUB.getDbKey(), p));
    }
}
