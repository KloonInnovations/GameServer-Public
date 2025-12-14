package io.kloon.gameserver.modes.creative.commands.test;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.storage.datainworld.PlayerWorldStorage;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WriteStuffCommand extends AdminCommand {
    public WriteStuffCommand() {
        super("writestuff");

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                PlayerWorldStorage storage = player.getInstance().getStorage(player);
                player.writeStuffToWorldStorage(storage);
                player.sendMessage(MM."<gold>Wrote!");
            }
        });
    }
}
