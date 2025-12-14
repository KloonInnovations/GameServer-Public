package io.kloon.gameserver.modes.creative.commands.menus;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.worldadmin.WorldAdminMenu;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class WorldAdminCommand extends Command {
    public static final String LABEL = "worldadmin";

    public WorldAdminCommand() {
        super(LABEL);
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativeMainMenu mainMenu = new CreativeMainMenu(player);
                WorldAdminMenu adminMenu = new WorldAdminMenu(mainMenu);
                adminMenu.display(player);
            }
        });
    }
}
