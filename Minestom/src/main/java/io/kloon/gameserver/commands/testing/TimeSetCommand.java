package io.kloon.gameserver.commands.testing;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentLong;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimeSetCommand extends AdminCommand {
    public TimeSetCommand() {
        super("timeset");

        ArgumentLong timeArg = ArgumentType.Long("time");
        addSyntax(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                if (!player.isAuthorized("commands.test")) return;

                long time = context.get(timeArg);
                player.getInstance().setTime(time);
                player.getInstance().setTimeRate(0);
                player.sendMessage("Time set to " + time);
            }
        }, timeArg);
    }
}
