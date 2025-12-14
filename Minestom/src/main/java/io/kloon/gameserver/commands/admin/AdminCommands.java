package io.kloon.gameserver.commands.admin;

import io.kloon.gameserver.commands.conditions.StaffRankCondition;
import io.kloon.infra.ranks.StaffRank;
import net.minestom.server.command.builder.Command;

public class AdminCommands extends Command {
    public AdminCommands() {
        super("admin");
        setCondition(new StaffRankCondition(StaffRank.ADMIN));
        addSubcommand(new StoreRankCommands());
        addSubcommand(new StaffRankCommands());
    }
}
