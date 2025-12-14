package io.kloon.gameserver.minestom.utils;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public final class RaycastUtils {
    @Nullable
    public static BlockVec getTargetBlock(Player player, double maxRange) {
        Instance instance = player.getInstance();
        Pos eyePos = player.getPosition().add(0, player.getEyeHeight(), 0);
        Vec dir = player.getPosition().direction();
        for (double i = 0.5; i <= maxRange; i += 0.5) {
            Point point = eyePos.add(dir.mul(i));
            Block block = instance.getBlock(point);
            if (!block.isAir()) {
                return new BlockVec(point);
            }
        }
        return null;
    }
}
