package io.kloon.gameserver.chestmenus.states;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.minestom.server.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ButtonStateTracker {
    private final Function<Player, ButtonState> stateFunction;
    private final Cache<UUID, ButtonState> stateByPlayer;

    public ButtonStateTracker(Function<Player, ButtonState> stateFunction) {
        this(stateFunction, 10_000);
    }

    public ButtonStateTracker(Function<Player, ButtonState> stateFunction, long retentionMs) {
        this.stateFunction = stateFunction;
        this.stateByPlayer = Caffeine.newBuilder()
                .expireAfterAccess(retentionMs, TimeUnit.MILLISECONDS)
                .build();
    }

    public boolean checkChanged(Player player) {
        ButtonState oldState = stateByPlayer.getIfPresent(player.getUuid());
        ButtonState newState = stateFunction.apply(player);

        stateByPlayer.put(player.getUuid(), newState);

        if (oldState == null) {
            return false;
        }

        return oldState != newState;
    }
}
