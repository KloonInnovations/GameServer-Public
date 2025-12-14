package io.kloon.gameserver.util.cooldowns.impl;

import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.infra.util.cooldown.impl.AbstractCooldown;

public class TickCooldown extends AbstractCooldown {
    public TickCooldown(long ticks) {
        super(ticks);
    }

    @Override
    protected long getNowTime() {
        return GlobalMinestomTicker.getTick();
    }
}
