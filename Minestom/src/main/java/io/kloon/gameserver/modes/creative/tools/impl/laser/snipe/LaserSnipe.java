package io.kloon.gameserver.modes.creative.tools.impl.laser.snipe;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.laser.LaserTool;
import io.kloon.gameserver.modes.creative.tools.impl.laser.LaserToolSettings;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.TargetBlockDisplay;
import io.kloon.gameserver.util.rendering.LineRender;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.kloon.gameserver.util.joml.JomlUtils.*;

public class LaserSnipe extends ToolSnipe<LaserToolSettings> {
    private final LaserTool tool;

    private TargetBlockDisplay targetDisplay;

    private final Map<Vector3f, LineRender> lines = new HashMap<>();

    private static final Vec UP = new Vec(0, 1, 0);
    private static List<Vector3f> OFFSETS = Arrays.asList(
            new Vector3f(1, 0, 0),
            new Vector3f(-1, 0, 0),
            new Vector3f(0, 1, 0),
            new Vector3f(0, -1, 0)
    );

    public LaserSnipe(CreativePlayer player, LaserTool tool) {
        super(player);
        this.tool = tool;
    }

    @Override
    protected void handleTick(BlockVec target, LaserToolSettings settings) {
        if (targetDisplay == null) {
            targetDisplay = new TargetBlockDisplay(this)
                    .withMaterial(Material.RED_STAINED_GLASS)
                    .withGlowColor(LaserTool.COLOR);
            targetDisplay.setInstance(instance);
        }

        int radius = settings.getRadius();

        Vec start = Vec.fromPoint(player.getEyePosition());
        Vec end = player.getPointInFront(player.getSnipe().getRange());
        Vec dir = end.sub(start).normalize();

        double length = start.distance(end);
        double offset = Math.min(length - 1, settings.getOffset());
        start = start.add(dir.mul(offset));
        end = end.add(dir.mul(1));

        Vector3f up = dir.equals(UP) || dir.equals(UP.neg())
                ? new Vector3f(0, 0, 1)
                : new Vector3f(0, 1, 0);

        for (Vector3f around : OFFSETS) {
            Matrix4f mat = new Matrix4f().rotationTowards(threef(dir), up);
            Vec rotated = unthreef(mat.transformPosition(around.mul(radius + 1, new Vector3f()), new Vector3f()));
            Vec aroundStart = start.add(rotated);
            Vec aroundEnd = end.add(rotated);
            LineRender lineRender = lines.computeIfAbsent(around, _ -> {
                LineRender render = new LineRender(aroundStart, aroundEnd)
                        .withMaterial(Material.RED_STAINED_GLASS)
                        .withGlowColor(LaserTool.COLOR);
                render.setInstance(instance);
                return render;
            });
            lineRender.adjust(aroundStart, aroundEnd);
        }
    }

    @Override
    protected void handleRemove() {
        if (targetDisplay != null) {
            targetDisplay.remove();
        }

        lines.values().forEach(LineRender::remove);
        lines.clear();
    }
}
