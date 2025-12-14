package io.kloon.gameserver.player.proxyinfo;

import io.kloon.infra.facts.KloonDataCenter;

public record KloonProxyInfo(KloonDataCenter datacenter, String allocationName) {
    public static final KloonProxyInfo DEFAULT = new KloonProxyInfo(KloonDataCenter.UNKNOWN, "unknown");
}
