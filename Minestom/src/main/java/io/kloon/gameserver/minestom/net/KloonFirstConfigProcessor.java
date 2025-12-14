package io.kloon.gameserver.minestom.net;

import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.network.player.PlayerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class KloonFirstConfigProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(KloonFirstConfigProcessor.class);
    private static final long TIMEOUT_MS = 5_000;

    private final ConcurrentHashMap<PlayerConnection, FirstConfig> firstConfigByConnection = new ConcurrentHashMap<>();

    public void runAsyncAfterLogin(PlayerConnection connection, String taskName, ConfigEventHandler task) {
        FirstConfig firstConfig = firstConfigByConnection.computeIfAbsent(connection, _ -> new FirstConfig());
        firstConfig.add(taskName, task);
    }

    public void tick() {
        long now = System.currentTimeMillis();
        firstConfigByConnection.values().removeIf(config -> {
            long elapsed = now - config.creation;
            return elapsed >= TIMEOUT_MS;
        });
    }

    public void handleFirstConfig(AsyncPlayerConfigurationEvent event) {
        if (!event.isFirstConfig()) {
            return;
        }

        Player player = event.getPlayer();
        if (!(player instanceof KloonPlayer kp)) {
            player.kick(MM."<red>Internal Error!");
            LOG.error("Wrong player type in first config processor! " + player);
            return;
        }

        PlayerConnection connection = player.getPlayerConnection();

        FirstConfig firstConfig = firstConfigByConnection.remove(connection);
        if (firstConfig == null) {
            player.kick(MM."<red>Internal Error!");
            LOG.error("Couldn't find first config data for " + player);
            return;
        }

        for (Map.Entry<String, ConfigEventHandler> entry : firstConfig.tasks.entrySet()) {
            String name = entry.getKey();
            ConfigEventHandler runnable = entry.getValue();

            try {
                runnable.handle(kp, event);
            } catch (Throwable t) {
                player.kick(MM."<red>Internal error!");
                LOG.error("Error with first-config task: \"" + name + "\"", t);
                return;
            }
        }
    }

    private static class FirstConfig {
        private final Map<String, ConfigEventHandler> tasks = Collections.synchronizedMap(new HashMap<>());
        private final long creation;

        private FirstConfig() {
            this.creation = System.currentTimeMillis();
        }

        public void add(String name, ConfigEventHandler task) {
            tasks.put(name, task);
        }
    }

    public interface ConfigEventHandler {
        void handle(KloonPlayer player, AsyncPlayerConfigurationEvent event);
    }
}
