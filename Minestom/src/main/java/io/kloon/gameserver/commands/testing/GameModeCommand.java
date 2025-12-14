package io.kloon.gameserver.commands.testing;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.input.NumberParsing;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class GameModeCommand extends AdminCommand {
    public GameModeCommand() {
        super("gamemode", "gm");

        ArgumentString gamemodeArg = ArgumentType.String("gamemode");
        addSyntax((sender, context) -> {
            if (!(sender instanceof KloonPlayer player)) return;
            if (!player.isAuthorized("essentials.gamemode")) return;

            String gamemodeInput = context.get(gamemodeArg);
            GameMode gameMode = parseGameMode(gamemodeInput);
            if (gameMode == null) {
                player.sendMessage(MM."<red>Unknown gamemode!");
                return;
            }

            player.setGameMode(gameMode);
            player.sendMessage(MM."<green>Set gamemode to <yellow>\{gameMode.name()}<green>!");
        }, gamemodeArg);
    }

    @Nullable
    private GameMode parseGameMode(String input) {
        if (NumberParsing.isInteger(input)) {
            int index = Integer.parseInt(input);
            return index < 0 || index >= VALUES.length ? null : VALUES[index];
        }
        return BY_NAME.get(input.toLowerCase());
    }

    private static final GameMode[] VALUES = GameMode.values();
    private static final EnumQuery<String, GameMode> BY_NAME = new EnumQuery<>(VALUES, g -> g.name().toLowerCase());
}
