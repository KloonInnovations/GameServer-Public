package io.kloon.gameserver.modes.creative.commands.enderchest;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.enderchest.EnderChestMenu;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class EnderChestCommand extends Command {
    public static final String LABEL = "enderchest";
    public static final String LABEL_SHORT = "echest";
    public static final String ONE = "e";

    public EnderChestCommand() {
        super(LABEL, LABEL_SHORT, ONE);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativeMainMenu mainMenu = new CreativeMainMenu(player);
                new EnderChestMenu(mainMenu, player).display(player);
            }
        });

        addSubcommand(new EnderChestSaveCommand());
    }
}
