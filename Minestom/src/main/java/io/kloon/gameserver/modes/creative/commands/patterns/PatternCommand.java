package io.kloon.gameserver.modes.creative.commands.patterns;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.patterns.PatternSelectionProxy;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.inventory.click.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PatternCommand extends Command {
    public static final String LABEL = "pattern";
    public static final String LABELS_ALT = "patterns";

    public PatternCommand() {
        super(LABEL, LABELS_ALT);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                new PatternSelectionProxy(player.createMainMenu()).clickButton(player, new ButtonClick(player, new Click.Left(-999)));
            }
        });
    }
}
