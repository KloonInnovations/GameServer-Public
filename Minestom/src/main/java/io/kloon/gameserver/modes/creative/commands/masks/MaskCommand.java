package io.kloon.gameserver.modes.creative.commands.masks;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.masks.api.GiveMaskExecutor;
import io.kloon.gameserver.modes.creative.commands.masks.api.MaskItemCommand;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskTypes;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.masks.MasksSelectionMenu;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MaskCommand extends Command {
    public static final String LABEL = "mask";
    public static final String MASKS_ALT = "masks";
    public static final String ONE_LETTER = "m";

    public MaskCommand() {
        super(LABEL, MASKS_ALT, ONE_LETTER);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativeMainMenu mainMenu = new CreativeMainMenu(player);
                new MasksSelectionMenu(mainMenu).display(player);
            }
        });

        for (MaskType<?> maskType : MaskTypes.getList()) {
            addSyntax(new GiveMaskExecutor(maskType, false), ArgumentType.Literal(maskType.getCommandLabel()));
            addSyntax(new GiveMaskExecutor(maskType, true), ArgumentType.Literal("!" + maskType.getCommandLabel()));

            List<Command> commands = maskType.createCommands();
            commands.forEach(command -> {
                if (command instanceof MaskItemCommand<?>) {
                    addSubcommand(command);
                }
            });
        }
    }
}
