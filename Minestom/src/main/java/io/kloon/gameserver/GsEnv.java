package io.kloon.gameserver;

import io.kloon.infra.util.EnvVar;

public final class GsEnv {
    public static final EnvVar USE_PROXY = new EnvVar("kloon.gs.minecraft.proxy", "KLOON_GS_MINECRAFT_PROXY");
    public static final EnvVar MINECRAFT_PORT = new EnvVar("kloon.gs.minecraft.port", "KLOON_GS_MINECRAFT_PORT");
    public static final EnvVar GAMEMODE = new EnvVar("kloon.gs.gamemode", "KLOON_GS_GAMEMODE");
    public static final EnvVar STATICWORLDS_PATH = new EnvVar("kloon.gs.staticworlds.path", "KLOON_GS_STATICWORLDS_PATH");
}
