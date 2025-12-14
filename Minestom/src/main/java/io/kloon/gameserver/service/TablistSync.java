package io.kloon.gameserver.service;

import com.github.benmanes.caffeine.cache.*;
import com.google.common.collect.Maps;
import io.kloon.gameserver.GameServerTopics;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.KloonInstance;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.tablist.*;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;
import io.kloon.infra.ranks.RankLooks;
import io.kloon.infra.serviceframework.NatsService;
import io.kloon.infra.serviceframework.annotations.Sub;
import io.kloon.infra.util.cooldown.CooldownMap;
import io.kloon.infra.util.cooldown.impl.TimeCooldown;
import io.nats.client.Connection;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket.*;

public class TablistSync extends NatsService {
    private static final Logger LOG = LoggerFactory.getLogger(TablistSync.class);

    private final CooldownMap<UUID, TimeCooldown> recentNetworkQuit = new CooldownMap<>(() -> new TimeCooldown(500, TimeUnit.MILLISECONDS));

    private Set<UUID> remoteUuids = new HashSet<>();
    private Set<UUID> localUuids = new HashSet<>();

    private final AsyncLoadingCache<UUID, Team> remoteTeams = Caffeine.newBuilder()
            .removalListener((RemovalListener<UUID, Team>) (uuid, team, removalCause) -> {
                if (team == null) return;
                MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> {
                    MinecraftServer.getTeamManager().deleteTeam(team);
                });
            })
            .buildAsync((uuid, executor) -> {
                return Kgs.getCaches().monikers().getByMinecraftUuid(uuid).thenApplyAsync(moniker -> {
                    String teamName = generateTeamName(moniker, true);
                    Team team = MinecraftServer.getTeamManager().createBuilder(teamName).build();
                    team.addMember(moniker.minecraftUsername());
                    return team;
                }, MinecraftServer.getSchedulerManager());
            });

    public TablistSync(Connection nats, Executor executor) {
        super(nats, executor);

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            List<KloonPlayer> localPlayers = Kgs.streamPlayers().toList();
            this.localUuids = localPlayers.stream().map(Player::getUuid).collect(Collectors.toSet());
            localPlayers.forEach(player -> {
                try {
                    refreshPlayer(player);
                } catch (Throwable t) {
                    LOG.error("Error refreshing player tablist", t);
                }
            });

            HashSet<UUID> teamUuids = new HashSet<>(remoteTeams.asMap().keySet());
            teamUuids.forEach(teamUuid -> {
                boolean nobodyNeedsTeam = localPlayers.stream().allMatch(kp -> kp.getTabList().getOfflineEntry(teamUuid) == null);
                if (nobodyNeedsTeam) {
                    remoteTeams.synchronous().invalidate(teamUuid);
                }
            });
        }, TaskSchedule.nextTick(), TaskSchedule.tick(10));
    }

    public void removeRemoteTeam(UUID playerId) {
        remoteTeams.synchronous().invalidate(playerId);
    }

    @EventHandler
    public void handleLocalJoin(PlayerSpawnEvent event) {
        KloonPlayer joiner = (KloonPlayer) event.getPlayer();

        localUuids.add(joiner.getUuid());

        event.getInstance().scheduleNextTick(inst -> {
            ((KloonInstance) inst).streamPlayers().forEach(this::refreshPlayer);
        });
    }

    /*@EventHandler
    public void fixPlayerQuit(PlayerDisconnectEvent event) {
        UUID playerId = event.getPlayer().getUuid();
        event.getInstance().scheduleNextTick(inst -> {
            if (recentNetworkQuit.get(playerId).isOnCooldown()) return;
            Kgs.streamPlayers().forEach(player -> {
                KloonTablist tablist = player.getTabList();
                Entry tempEntry = tablist.getOnlineEntry(event.getPlayer());
                player.getVirtualTablist().put(tempEntry, true);
            });
        });
    }*/

    @Sub(topic = GameServerTopics.TABLIST_STATE)
    public void onTablistState(TablistState packet) {
        this.remoteUuids = new HashSet<>(packet.minecraftUuids());
        Map<UUID, KloonPlayer> localPlayers = Maps.uniqueIndex(Kgs.streamPlayers().toList(), Player::getUuid);
        this.localUuids = new HashSet<>(localPlayers.keySet());

        Kgs.streamPlayers().forEach(this::refreshPlayer);
    }

    private void refreshPlayer(KloonPlayer player) {
        KloonTablist tablist = player.getTabList();
        
        tablist.purgeUnknowns(localUuids, remoteUuids);

        localUuids.forEach(uuid -> {
            Player target = player.getInstance().getPlayerByUuid(uuid);
            if (target == null) return;
            Entry entry = tablist.getOnlineEntry(target);
            tablist.put(entry);
        });

        remoteUuids.forEach(uuid -> {
            if (tablist.has(uuid)) return;
            CompletableFuture<Entry> future = tablist.getOfflineEntry(uuid);
            remoteTeams.get(uuid);
            tablist.put(uuid, future);
        });

        Collection<Entry> entriesAsLoaded = tablist.getEntriesAsLoaded();
        player.getVirtualTablist().set(entriesAsLoaded, true);
    }

    @Sub(topic = GameServerTopics.TABLIST_UPDATE)
    public void onFastTablistChange(TablistUpdate packet) {
        if (packet.getAction() == TablistUpdate.Action.ADD) {
            handleNetworkTablistAdd(packet.getUuid());
        } else {
            handleNetworkTablistRemove(packet.getUuid());
        }
    }

    private void handleNetworkTablistAdd(UUID uuid) {
        remoteUuids.add(uuid);
        Kgs.streamPlayers().forEach(player -> {
            KloonTablist tablist = player.getTabList();
            CompletableFuture<Entry> future = tablist.getOfflineEntry(uuid);
            remoteTeams.get(uuid);
            future.thenAcceptAsync(entry -> {
                VirtualTablist vTablist = player.getVirtualTablist();
                vTablist.put(entry, true);
            }, player.scheduler());
        });
    }

    private void handleNetworkTablistRemove(UUID uuid) {
        remoteUuids.remove(uuid);
        recentNetworkQuit.get(uuid).cooldown();
        Kgs.streamPlayers().forEach(player -> {
            VirtualTablist vTablist = player.getVirtualTablist();
            vTablist.remove(uuid, true);
        });
    }

    // team name determines tablist sort
    public static String generateTeamName(KloonMoniker moniker, boolean virtual) {
        try {
            RankLooks looks = moniker.ranks().getBestRankLooks();
            return looks.teamNameChar() + moniker.getName() + (virtual ? "_v" : "");
        } catch (Throwable t) {
            LOG.error("Error generating team name", t);
            return moniker.minecraftUuid().toString();
        }
    }
}
