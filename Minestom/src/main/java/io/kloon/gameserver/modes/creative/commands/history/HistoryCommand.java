package io.kloon.gameserver.modes.creative.commands.history;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.history.HistoryMenu;
import io.kloon.gameserver.modes.creative.tools.impl.history.HistoryTool;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class HistoryCommand extends Command {
    public static final String LABEL = "history";

    public HistoryCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                openHistoryMenu(player);
            }
        });
    }

    public static void openHistoryMenu(CreativePlayer player) {
        HistoryTool historyTool = player.getCreative().getHistoryTool();
        CreativeMainMenu mainMenu = new CreativeMainMenu(player);
        new HistoryMenu(historyTool, mainMenu, player).display(player);
    }
}
