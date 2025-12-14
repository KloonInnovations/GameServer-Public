package io.kloon.gameserver.modes.creative.commands.test;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ClearAuditHistoryCommand extends AdminCommand {
    public ClearAuditHistoryCommand() {
        super("clearaudithistory");

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                player.getInstance().getWorldStorage().getAuditHistory().clear();
                player.sendMessage(MM."<green>Cleared!");
            }
        });
    }
}
