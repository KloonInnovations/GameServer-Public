package io.kloon.gameserver.modes.creative.storage.datainworld.minestom;

import net.minestom.server.coordinate.Pos;

public record StoragePos(double x, double y, double z, float yaw, float pitch) {
    public StoragePos(Pos pos) {
        this(pos.x(), pos.y(), pos.z(), pos.yaw(), pos.pitch());
    }

    public Pos toPos() {
        return new Pos(x, y, z, yaw, pitch);
    }
}
