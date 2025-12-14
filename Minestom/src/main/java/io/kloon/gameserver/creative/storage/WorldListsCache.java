package io.kloon.gameserver.creative.storage;

import com.github.benmanes.caffeine.cache.*;
import com.google.common.collect.Maps;
import io.kloon.bigbackend.client.games.CreativeClient;
import io.kloon.bigbackend.games.creative.InvalidateCreativeWorld;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.infra.serviceframework.subscriptions.PredicatedSubscription;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class WorldListsCache {
    private final CreativeClient creativeClient;
    private final CreativeWorldsRepo repo;

    private final ConcurrentHashMap<ObjectId, PredicatedSubscription<InvalidateCreativeWorld>> subsByWorldId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ObjectId, PredicatedSubscription<InvalidateCreativeWorld>> subsByOwnerId = new ConcurrentHashMap<>();

    private final AsyncLoadingCache<WorldOwner, Map<ObjectId, WorldDef>> cacheByOwner = Caffeine.newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .evictionListener(new RemovalListener<WorldOwner, Map<ObjectId, WorldDef>>() {
                @Override
                public void onRemoval(WorldOwner worldOwner, Map<ObjectId, WorldDef> worlds, RemovalCause removalCause) {
                    worlds.values().forEach(world -> {
                        PredicatedSubscription<InvalidateCreativeWorld> sub = subsByWorldId.remove(world._id());
                        if (sub == null) return;
                        sub.cancel();
                    });

                    ObjectId ownerId = worldOwner.asOwnership().playerId();
                    if (ownerId != null) {
                        PredicatedSubscription<InvalidateCreativeWorld> sub = subsByOwnerId.remove(ownerId);
                        if (sub != null) {
                            sub.cancel();
                        }
                    }
                }
            })
            .buildAsync(new AsyncCacheLoader<>() {
                @Override
                public CompletableFuture<? extends Map<ObjectId, WorldDef>> asyncLoad(WorldOwner worldOwner, Executor executor) throws Exception {
                    CompletableFuture<List<WorldDef>> getWorlds = repo.defs().getWorldsByOwner(worldOwner);
                    getWorlds.thenAccept(defs -> defs.forEach(def -> registerNetworkSub(def, executor)));
                    return getWorlds.thenApply(list -> new ConcurrentHashMap<>(Maps.uniqueIndex(list, WorldDef::_id)));
                }
            });

    public WorldListsCache(CreativeClient creativeClient, CreativeWorldsRepo repo) {
        this.creativeClient = creativeClient;
        this.repo = repo;
    }

    public void invalidate(WorldOwner owner) {
        cacheByOwner.synchronous().invalidate(owner);
    }

    private void registerNetworkSub(WorldDef def, Executor executor) {
        subsByWorldId.computeIfAbsent(def._id(), worldId -> {
            return creativeClient.worldInvalidateSub(executor, worldId,
                    () -> isCached(def),
                    _ -> repo.defs().getWorldDef(worldId).thenAccept(this::replace));
        });

        ObjectId ownerId = def.ownership().playerId();
        if (ownerId != null) {
            subsByOwnerId.computeIfAbsent(ownerId, _ -> {
                return creativeClient.ownerInvalidateSub(executor, ownerId,
                        () -> subsByOwnerId.containsKey(ownerId),
                        _ -> cacheByOwner.synchronous().invalidate(def.owner()));
            });
        }
    }

    private boolean isCached(WorldDef world) {
        WorldOwner worldOwner = world.owner();
        Map<ObjectId, WorldDef> worldDefs = cacheByOwner.synchronous().get(worldOwner);
        return worldDefs != null && worldDefs.containsKey(world._id());
    }

    private void replace(WorldDef world) {
        WorldOwner worldOwner = world.owner();
        Map<ObjectId, WorldDef> worlds = cacheByOwner.synchronous().getIfPresent(worldOwner);
        if (worlds == null) return;
        worlds.put(world._id(), world);
    }

    public CompletableFuture<List<WorldDef>> get(WorldOwner owner) {
        return cacheByOwner.get(owner).thenApply(map -> new ArrayList<>(map.values()));
    }

    public WorldDef getWorldIfPresent(WorldOwner owner, ObjectId worldId) {
        Map<ObjectId, WorldDef> ownerWorlds = cacheByOwner.synchronous().getIfPresent(owner);
        if (ownerWorlds == null) return null;
        return ownerWorlds.get(worldId);
    }
}
