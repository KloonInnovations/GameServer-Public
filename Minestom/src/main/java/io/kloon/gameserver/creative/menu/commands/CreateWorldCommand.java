package io.kloon.gameserver.creative.menu.commands;

import io.kloon.bigbackend.client.BigBackendClient;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.defs.WorldDefRepo;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.creative.storage.saves.WorldSaveRepo;
import io.kloon.gameserver.creative.storage.saves.WorldSaveWithData;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.KloonNetworkInfra;
import io.kloon.infra.facts.KloonDataCenter;
import io.kloon.infra.ranks.PlayerRankCache;
import io.kloon.infra.ranks.StoreRank;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreateWorldCommand {
    private static final Logger LOG = LoggerFactory.getLogger(CreateWorldCommand.class);

    public static void createWorld(KloonPlayer player, WorldDef def) {
        createWorldWithCopy(player, def, null);
    }

    public static void createWorldWithCopy(KloonPlayer player, WorldDef def, @Nullable WorldSaveWithData saveToCopy) {
        WorldDefRepo defsRepo = Kgs.getCreativeRepos().defs();

        BigBackendClient backend = Kgs.getBackend();

        int worldsLimit = getWorldsLimit(player);

        WorldOwner owner = def.owner();
        owner.runWithLock(Kgs.getInfra(), player, () -> {
            return defsRepo.countLiveWorldsByOwner(owner).thenComposeAsync(count -> {
                if (count + 1 > worldsLimit) {
                    player.scheduleNextTick(e -> {
                        player.sendMessage(MM."<red>Maximum number of worlds reached!");
                    });
                    return CompletableFuture.completedFuture(null);
                }

                CompletableFuture<Void> insertDef = defsRepo.insert(def);
                CompletableFuture<?> copySave = copySave(def._id(), def.datacenter(), saveToCopy);

                return CompletableFuture.allOf(insertDef, copySave).whenCompleteAsync((_, t) -> {
                    if (t != null) {
                        player.sendMessage(MM."<red>There was an error creating the world, try again later!");
                        LOG.error(STR."Error creating world for \{player}", t);
                        return;
                    }

                    def.broadcastInvalidate();

                    if (saveToCopy != null) {
                        player.sendMessage(MM."<green>Copied the world!");
                    }
                    player.allocateAndTransfer(p -> backend.getCreative().allocLatestSaveTransfer(p, def._id(), def.datacenter()));
                }, player.scheduler());
            });
        });
    }

    private static CompletableFuture<WorldSaveWithData> copySave(ObjectId worldId, KloonDataCenter datacenter, @Nullable WorldSaveWithData saveToCopy) {
        if (saveToCopy == null) {
            return CompletableFuture.completedFuture(null);
        }

        KloonNetworkInfra infra = Kgs.getInfra();

        WorldSaveRepo savesRepo = new WorldSaveRepo(Kgs.getInfra(), datacenter.getCreativeWorldsBucket());
        WorldSave copy = new WorldSave(new ObjectId(), worldId, System.currentTimeMillis(), WorldSave.VERSION, WorldSave.Reason.WORLD_COPIED,
                null, 0, infra.allocationName(), infra.serverName(), null, saveToCopy.worldSave()._id());
        return savesRepo.saveData(copy, saveToCopy.polarBytes(), saveToCopy.customBytes()).thenApply(saveWithData -> {
            LOG.info(STR."Saved world copy, oldSaveId=\{saveToCopy.worldSave()._id()} newSaveId=\{saveWithData.worldSave()._id()}");
            return saveWithData;
        });
    }

    public static int getWorldsLimit(KloonPlayer player) {
        PlayerRankCache ranks = player.getRanks();
        if (ranks.hasProSub()) {
            return 500;
        }

        int limit = 3;
        if (ranks.hasExactStoreRank(StoreRank.EARLY_ADOPTER)) {
            limit += 30;
        }

        return limit;
    }
}
