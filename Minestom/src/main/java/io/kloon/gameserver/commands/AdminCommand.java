package io.kloon.gameserver.commands;

import io.kloon.gameserver.commands.conditions.StaffRankCondition;
import io.kloon.infra.ranks.StaffRank;
import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AdminCommand extends Command {
    public AdminCommand(@NotNull String name, @Nullable String... aliases) {
        super(name, aliases);
        setCondition(new StaffRankCondition(StaffRank.ADMIN));
    }

    public AdminCommand(@NotNull String name) {
        super(name);
        setCondition(new StaffRankCondition(StaffRank.ADMIN));
    }
}
