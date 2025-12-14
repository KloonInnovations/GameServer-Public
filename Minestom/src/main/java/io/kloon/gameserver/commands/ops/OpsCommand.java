package io.kloon.gameserver.commands.ops;

import io.kloon.gameserver.commands.AdminCommand;

public class OpsCommand extends AdminCommand {
    public OpsCommand() {
        super("ops");
        addSubcommand(new OpsInfoCommand());
    }
}
