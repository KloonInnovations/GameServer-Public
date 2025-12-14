package io.kloon.gameserver.backend;

import io.kloon.gameserver.modes.ModeType;
import io.kloon.infra.facts.KloonDataCenter;

public record GameServerInfo(
        String cuteName,
        String allocationName,
        int minecraftPort,
        KloonDataCenter datacenter,
        ModeType gamemode,
        long startTimestamp,
        boolean proxied
) {
}
