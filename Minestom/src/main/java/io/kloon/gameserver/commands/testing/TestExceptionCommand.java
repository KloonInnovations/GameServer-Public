package io.kloon.gameserver.commands.testing;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExceptionCommand extends AdminCommand {
    private static final Logger LOG = LoggerFactory.getLogger(TestExceptionCommand.class);

    public TestExceptionCommand() {
        super("testexception");

        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                LOG.error("Oh no there was an error!", new RuntimeException("Test exception"));
                player.sendMessage("Logged exception in console!");
            }
        });
    }
}
