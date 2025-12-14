package io.kloon.gameserver.chestmenus;

import io.kloon.gameserver.chestmenus.signui.SignUX;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.network.packet.server.SendablePacket;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ChestMenuPlayer {
    UUID getUuid();

    SignUX getSignUX();

    void setSignUX(@Nullable SignUX signUX);

    void sendPacket(SendablePacket packet);

    Pos getPosition();
}