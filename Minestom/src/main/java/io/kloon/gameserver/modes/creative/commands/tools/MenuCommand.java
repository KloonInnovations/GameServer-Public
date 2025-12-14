package io.kloon.gameserver.modes.creative.commands.tools;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.tools.impl.MainMenuTool;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class MenuCommand extends Command {
    public static final String LABEL = "menu";
    
    private final MainMenuTool mainMenuTool;

    public MenuCommand(MainMenuTool mainMenuTool) {
        super(LABEL);
        this.mainMenuTool = mainMenuTool;

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                mainMenuTool.displayMenu(player);
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        });
    }
}
