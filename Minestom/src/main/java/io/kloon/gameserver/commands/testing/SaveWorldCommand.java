package io.kloon.gameserver.commands.testing;

import io.kloon.gameserver.commands.AdminCommand;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SaveWorldCommand extends AdminCommand {
    private static final Logger LOG = LoggerFactory.getLogger(SaveWorldCommand.class);

    public SaveWorldCommand() {
        super("saveworld");
        setDefaultExecutor((sender, ctx) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MM."<You need to be a player to use this command!>");
                return;
            }

            long startMs = System.currentTimeMillis();
            Instance instance = player.getInstance();
            instance.saveInstance().thenRunAsync(() -> {
                long elapsed = System.currentTimeMillis() - startMs;
                player.sendMessage(MM."<green>Saved the instance with <aqua>\{instance.getChunks().size()} <green>chunks in <white>\{elapsed}ms<green>!");
            }, player.scheduler()).exceptionally(t -> {
                player.sendMessage(MM."<red>An error occurred!");
                LOG.error("Error saving world with command", t);
                return null;
            });
        });
    }
}
