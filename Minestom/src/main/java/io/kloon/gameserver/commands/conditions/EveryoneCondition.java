package io.kloon.gameserver.commands.conditions;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.condition.CommandCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EveryoneCondition implements CommandCondition {
    @Override
    public boolean canUse(@NotNull CommandSender sender, @Nullable String commandString) {
        return true;
    }
}
