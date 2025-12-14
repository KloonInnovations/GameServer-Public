package io.kloon.gameserver.commands.testing;

import io.kloon.gameserver.MiniMessageTemplate;
import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;

public class MMCommand extends AdminCommand {
    public MMCommand() {
        super("mm");
        ArgumentStringArray mmArg = ArgumentType.StringArray("mini message");
        addSyntax(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                String mmStr = String.join(" ", context.get(mmArg));
                player.sendMessage(MiniMessageTemplate.miniMessage.deserialize(mmStr));
            }
        }, mmArg);
    }
}
