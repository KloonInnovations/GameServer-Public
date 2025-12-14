package io.kloon.gameserver.commands.moderation;

import io.kloon.gameserver.commands.conditions.StaffRankCondition;
import io.kloon.infra.ranks.StaffRank;
import net.minestom.server.command.builder.Command;

public class ModeratorCommands extends Command {
    public ModeratorCommands() {
        super("mod");
        setCondition(new StaffRankCondition(StaffRank.MODERATOR));

        addSubcommand(new BanCommand());
        addSubcommand(new ModLookupCommand());
    }
}
