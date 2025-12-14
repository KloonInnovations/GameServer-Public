package io.kloon.gameserver.backend;

import io.kloon.bigbackend.client.admin.GameServerSyncClient;
import io.kloon.bigbackend.gameservers.GameServerHeartbeat;
import io.kloon.bigbackend.gameservers.InstanceHeartbeat;
import io.kloon.gameserver.creative.storage.CreativeChunkLoader;
import io.kloon.gameserver.minestom.KloonInstance;
import io.nats.client.Connection;
import net.hollowcube.polar.minestom.FilePolarChunkLoader;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

public class ServerSyncTask implements Supplier<TaskSchedule> {
    private static final Logger LOG = LoggerFactory.getLogger(ServerSyncTask.class);

    private final GameServerSyncClient syncClient;
    private final GameServerInfo serverInfo;
    private final InstanceManager instanceMan;

    public ServerSyncTask(Connection nats, GameServerInfo serverInfo, InstanceManager instanceMan) {
        this.syncClient = new GameServerSyncClient(nats);
        this.serverInfo = serverInfo;
        this.instanceMan = instanceMan;
    }

    @Override
    public TaskSchedule get() {
        try {
            List<InstanceHeartbeat> instances = instanceMan.getInstances().stream().map(this::toInstanceHeartbeat).toList();

            GameServerHeartbeat heartbeat = new GameServerHeartbeat(
                    serverInfo.cuteName(),
                    serverInfo.allocationName(),
                    serverInfo.minecraftPort(),
                    serverInfo.datacenter().getDbKey(),
                    serverInfo.gamemode().getDbKey(),
                    instances,
                    serverInfo.startTimestamp(),
                    System.currentTimeMillis()
            );

            syncClient.heartbeat(heartbeat);
        } catch (Throwable t) {
            LOG.error("Error in server sync task", t);
        }
        return TaskSchedule.tick(20);
    }

    private InstanceHeartbeat toInstanceHeartbeat(Instance instance) {
        List<InstanceHeartbeat.Player> players = instance.getPlayers().stream().map(player -> {
            return new InstanceHeartbeat.Player(
                    player.getUuid(),
                    player.getUsername()
            );
        }).toList();

        String world = getWorldString(instance);
        String cuteName = instance instanceof KloonInstance kInstance
                ? kInstance.getCuteName()
                : instance.getUniqueId().toString();

        return new InstanceHeartbeat(instance.getUniqueId(), world, cuteName, players);
    }

    private String getWorldString(Instance instance) {
        if (!(instance instanceof InstanceContainer ic)) {
            return "unknown";
        }

        IChunkLoader chunkLoader = ic.getChunkLoader();
        return switch (chunkLoader) {
            case FilePolarChunkLoader file -> file.getPath().getFileName().toString();
            case CreativeChunkLoader creative -> creative.getWorldDef().idHex();
            default -> "unknown";
        };
    }
}
