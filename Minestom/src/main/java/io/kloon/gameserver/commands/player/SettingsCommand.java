package io.kloon.gameserver.commands.player;

import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public abstract class SettingsCommand extends Command {
    public static final String LABEL = "settings";

    public SettingsCommand() {
        super(LABEL);

        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                openMenu(player);
            }
        });
    }

    public abstract void openMenu(KloonPlayer player);
}
