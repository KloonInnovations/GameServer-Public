package io.kloon.gameserver.modes.creative.commands.test;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.creative.storage.CreativeChunkLoader;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.SaveCreativeWorldCommand;
import io.kloon.infra.objectstorage.KloonBucket;
import io.kloon.infra.objectstorage.ObjectStorageBucket;
import net.hollowcube.polar.minestom.integration.InMemoryPolarWorld;
import net.kyori.adventure.text.event.ClickEvent;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreateWorldLinkCommand extends AdminCommand {
    public CreateWorldLinkCommand() {
        super("createworldlink");
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativeChunkLoader chunkLoader = player.getInstance().getChunkLoader();
                if (!(chunkLoader instanceof CreativeChunkLoader creativeLoader)) {
                    player.sendMessage(MM."<red>Unknown chunk loader!");
                    return;
                }

                CompletableFuture<InMemoryPolarWorld> future = creativeLoader.getLoadingFuture();
                if (!future.isDone()) {
                    player.sendMessage(MM."<red>Somehow the world is still loading!");
                    return;
                }

                WorldSave latestSave = creativeLoader.getLatestSave();
                if (latestSave == null) {
                    player.sendMessage(MM."<red>No known save, try /\{SaveCreativeWorldCommand.LABEL} first!");
                    return;
                }

                InMemoryPolarWorld polarWorld = future.join();
                byte[] polarBytes = polarWorld.getSaver().saveChunks();

                ObjectStorageBucket bucket = Kgs.getInfra().getBucket(KloonBucket.DO_WORLD_LINKS_US_EAST);
                String objectId = latestSave.hexId();

                String url = STR."https://world-links.nyc3.digitaloceanspaces.com/\{objectId }";

                CompletableFuture<Void> upload = bucket.upload(objectId, polarBytes);
                CompletableFuture<Void> makePublic = upload.thenCompose(_ -> bucket.setReadPermission(objectId, true));

                CompletableFuture.allOf(upload, makePublic).thenRunAsync(() -> {
                    player.sendMessage(MM."<yellow><bold>CLICK TO DOWNLOAD!"
                            .hoverEvent(MM."<yellow>Click to copy url!")
                            .clickEvent(ClickEvent.openUrl(url)));
                }, player.scheduler());
            }
        });
    }
}