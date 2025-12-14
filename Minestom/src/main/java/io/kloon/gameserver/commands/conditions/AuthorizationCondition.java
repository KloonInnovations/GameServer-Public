package io.kloon.gameserver.commands.conditions;

import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.condition.CommandCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AuthorizationCondition implements CommandCondition {
    private final String authString;

    public AuthorizationCondition(String authString) {
        this.authString = authString;
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender, @Nullable String commandString) {
        if (!(sender instanceof KloonPlayer kp)) return false;
        return kp.isAuthorized(authString);
    }
}
