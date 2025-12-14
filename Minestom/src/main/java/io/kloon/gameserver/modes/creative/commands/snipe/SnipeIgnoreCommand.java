package io.kloon.gameserver.modes.creative.commands.snipe;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggleButton;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class SnipeIgnoreCommand extends Command {
    public static final String LABEL = "ignore";
    public static final String FULL = SnipeCommand.LABEL + " " + LABEL;

    public SnipeIgnoreCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                new PlayerStorageToggleButton(0, SnipeSettingsMenu.IGNORE_BLOCKS).toggle(player);
            }
        });
    }
}
