package io.kloon.gameserver.modes.creative.commands.test;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.creative.storage.CreativeChunkLoader;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import net.kyori.adventure.text.event.ClickEvent;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WorldIdCommand extends AdminCommand {
    public WorldIdCommand() {
        super("worldid");

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativeInstance instance = player.getInstance();

                String worldId = instance.getWorldDef().idHex();
                player.sendMessage(MM."<white>World ID: <green>\{worldId}"
                        .hoverEvent(MM."<yellow>Click to copy world id!")
                        .clickEvent(ClickEvent.copyToClipboard(worldId)));

                CreativeChunkLoader creativeChunkLoader = instance.getChunkLoader();
                WorldSave latestSave = creativeChunkLoader.getLatestSave();
                if (latestSave == null) {
                    player.sendMessage(MM."<white>Latest save ID: <red>None!");
                } else {
                    String saveId = latestSave.hexId();
                    player.sendMessage(MM."<white>Latest save ID: <gold>\{saveId }"
                            .hoverEvent(MM."<yellow>Click to copy save id!")
                            .clickEvent(ClickEvent.copyToClipboard(saveId)));
                }
            }
        });
    }
}
