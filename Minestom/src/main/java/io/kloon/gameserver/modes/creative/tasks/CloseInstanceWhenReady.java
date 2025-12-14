package io.kloon.gameserver.modes.creative.tasks;

import io.kloon.gameserver.modes.creative.CreativeInstance;

public class CloseInstanceWhenReady implements Runnable {
    private final CreativeInstance instance;

    public CloseInstanceWhenReady(CreativeInstance instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        if (isReadyToClose()) {
            instance.close();
        }
    }

    private boolean isReadyToClose() {
        return instance.getPlayers().isEmpty();
    }
}
