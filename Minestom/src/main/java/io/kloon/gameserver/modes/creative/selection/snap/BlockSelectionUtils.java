package io.kloon.gameserver.modes.creative.selection.snap;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class BlockSelectionUtils {
    public static Vec findSelectedBlock(Player player) {
        return raycast(player, 5.0).apply(Vec.Operator.FLOOR);
    }

    public static Vec findBlockAtRange(Player player, double range) {
        Vec eyePos = player.getPosition().add(0, player.getEyeHeight(), 0).asVec();
        Vec dir = player.getPosition().direction();
        return eyePos.add(dir.mul(range));
    }

    private static Vec raycast(Player player, double maxRange) {
        Instance instance = player.getInstance();
        Vec eyePos = player.getPosition().add(0, player.getEyeHeight(), 0).asVec();
        Vec dir = player.getPosition().direction();
        for (double range = 0; range <= maxRange; range += 0.3) {
            Vec check = eyePos.add(dir.mul(range));
            Block block = instance.getBlock(check);
            if (!block.isAir()) {
                return check;
            }
        }
        return eyePos.add(dir.mul(maxRange));
    }
}
