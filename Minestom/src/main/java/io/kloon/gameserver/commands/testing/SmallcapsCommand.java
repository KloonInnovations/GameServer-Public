package io.kloon.gameserver.commands.testing;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.infra.util.SmallCaps;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class SmallcapsCommand extends AdminCommand {
    public SmallcapsCommand() {
        super("smallcaps");
        ArgumentStringArray textArg = ArgumentType.StringArray("text");
        addSyntax((sender, context) -> {
            String text = String.join(" ", context.get(textArg));
            String smallcaps = SmallCaps.convert(text);
            sender.sendMessage(smallcaps);
        }, textArg);
    }
}
