package io.kloon.gameserver.modes.creative.commands.tools;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.clipboard.ClipboardMenu;
import io.kloon.gameserver.modes.creative.tools.ToolsListener;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class ClipboardCommand extends Command {
    public static final String LABEL = "clipboard";

    public ClipboardCommand() {
        super(LABEL);
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                ToolsListener tools = player.getCreative().getToolsListener();

                CreativeMainMenu mainMenu = new CreativeMainMenu(player);
                new ClipboardMenu(mainMenu, tools).display(player);
            }
        });
    }
}
