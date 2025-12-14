package io.kloon.gameserver.modes;

import io.kloon.gameserver.modes.creative.CreativeMode;
import io.kloon.gameserver.modes.impl.DevMode;
import io.kloon.gameserver.modes.hub.HubMode;
import io.kloon.infra.KloonNetworkInfra;

public class GameModeFactory {
    public GameServerMode create(ModeType modeType, KloonNetworkInfra infra) {
        if (!modeType.canBootOn(infra.environment())) {
            throw new RuntimeException(STR."Cannot boot mode \{modeType} on environment \{infra.environment()}");
        }

        return switch (modeType) {
            case HUB -> new HubMode(infra);
            case CREATIVE -> new CreativeMode(infra);
            case DEV -> new DevMode(infra);
            default -> throw new IllegalStateException(STR."We don't know how to boot mode \{modeType}");
        };
    }
}
