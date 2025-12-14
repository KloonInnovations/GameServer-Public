package io.kloon.gameserver.commands.testing;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.modes.creative.menu.patterns.BlockSelectionMenu;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class TestBlockSelectionCommand extends AdminCommand {
    public TestBlockSelectionCommand() {
        super("testblockselection");
        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer kp, @NotNull CommandContext context) {
                new BlockSelectionMenu(null, (player, block) -> {
                    player.sendPit(NamedTextColor.GREEN, "SELECTED!", MM."<white>Block: <green>\{block}");
                    player.closeInventory();
                }).display(kp);
            }
        });
    }
}
