package io.kloon.gameserver.modes.creative.tools.snipe;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.coordinate.BlockVec;

public abstract class ToolSnipe<ItemBound> {
    protected final CreativePlayer player;
    protected final CreativeInstance instance;

    private BlockVec target;

    protected boolean removed = false;

    public ToolSnipe(CreativePlayer player) {
        this.player = player;
        this.instance = player.getInstance();
    }

    public boolean isValid() {
        return player.getInstance() == instance && player.isOnline() && !removed;
    }

    public BlockVec getTarget() {
        return target;
    }

    public final void tick(BlockVec target, ItemBound settings) {
        this.target = target;
        if (!isValid()) {
            remove();
            return;
        }
        handleTick(target, settings);
    }

    protected abstract void handleTick(BlockVec target, ItemBound settings);

    public final void remove() {
        if (removed) {
            return;
        }
        this.removed = true;
        handleRemove();
    }

    protected abstract void handleRemove();
}
