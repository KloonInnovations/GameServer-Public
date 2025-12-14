package io.kloon.gameserver.modes.creative.commands.snipe;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.RangeInputButton;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import org.jetbrains.annotations.NotNull;

public class SnipeCommand extends Command {
    public static final String LABEL = "snipe";

    public SnipeCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativeMainMenu mainMenu = new CreativeMainMenu(player);
                SnipeSettingsMenu snipeSettingsMenu = new SnipeSettingsMenu(mainMenu.getSettingsMenu());
                snipeSettingsMenu.display(player);
            }
        });

        ArgumentDouble rangeArg = ArgumentType.Double("range (blocks)");
        addSyntax(new RangeExecutor(rangeArg), rangeArg);
        addSyntax(new RangeExecutor(rangeArg), ArgumentType.Literal("range"), rangeArg);

        addSubcommand(new SnipeIgnoreCommand());
    }

    public static class RangeExecutor extends CreativeExecutor {
        private final ArgumentDouble rangeArg;

        public RangeExecutor(ArgumentDouble rangeArg) {
            this.rangeArg = rangeArg;
        }

        @Override
        public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
            double range = context.get(rangeArg);
            new RangeInputButton(0).setValue(player, range);
        }
    }
}
