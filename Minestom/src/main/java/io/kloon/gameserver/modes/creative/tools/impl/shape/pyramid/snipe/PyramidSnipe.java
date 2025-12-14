package io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.snipe;

import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.PyramidTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.PyramidToolSettings;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.TargetBlockDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;
import io.kloon.gameserver.util.rendering.LineRender;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.Material;

import java.util.*;

public class PyramidSnipe extends ToolSnipe<PyramidToolSettings> {
    private final PyramidTool tool;

    private TargetBlockDisplay targetDisplay;

    private final Map<RenderedEdge, LineRender> lines = new HashMap<>();

    private static final List<Edge> EDGES = Arrays.asList(
            new Edge(0, 0, 0, 1),
            new Edge(1, 0, -1, 0),
            new Edge(1, 1, 0, -1),
            new Edge(0, 1, 1, 0)
    );

    public PyramidSnipe(CreativePlayer player, PyramidTool tool) {
        super(player);
        this.tool = tool;
    }

    @Override
    protected void handleTick(BlockVec target, PyramidToolSettings settings) {
        SnipeVisibility vis = player.getSnipe().getVisibility();

        if (targetDisplay == null) {
            targetDisplay = new TargetBlockDisplay(this)
                    .withMaterial(vis.editMat(Material.YELLOW_STAINED_GLASS))
                    .withGlowColor(vis.editGlow(new Color(255, 255, 0)));
            targetDisplay.setInstance(instance);
        }

        if (!vis.isVisible()) {
            lines.values().forEach(LineRender::remove);
            lines.clear();
        }

        if (vis.isVisible()) {
            BoundingBox boundingBox = settings.createGenSettings(target, player.computeMaskLookup()).computeBoundingBox();

            Point top = BoundingBoxUtils.getCenter(boundingBox).withY(boundingBox.maxY());
            Vec below = new Vec(0, -1, 0);

            if (settings.isUpsideDown()) {
                top = top.withY(boundingBox.minY());
                below = new Vec(0, 1, 0);
            }

            for (Edge edge : EDGES) {
                Point a = edge.computeA(boundingBox);
                Point b = edge.computeB(boundingBox);

                if (settings.isUpsideDown()) {
                    a = a.withY(boundingBox.maxY() - 1);
                    b = b.withY(boundingBox.maxY() - 1);
                }

                renderEdge(edge, vis, true, false, false, a, top);
                renderEdge(edge, vis, false, false, true, a, b);
                renderEdge(edge, vis, false, false, false, a, a.add(below));
                renderEdge(edge, vis, false, true, true, a.add(below), b.add(below));
            }
        }
    }

    private void renderEdge(Edge edge, SnipeVisibility vis, boolean aimingTop, boolean sub, boolean side, Point a, Point b) {
        RenderedEdge rendered = new RenderedEdge(edge, aimingTop, sub, side);
        LineRender lineRender = lines.computeIfAbsent(rendered, _ -> LineRender.spawn(instance, a, b));
        lineRender
                .withMaterial(vis.editMat(Material.YELLOW_STAINED_GLASS))
                .withGlowColor(vis.editGlow(new Color(255, 255, 0)))
                .adjust(a, b);
    }

    @Override
    protected void handleRemove() {
        if (targetDisplay != null) {
            targetDisplay.remove();
        }

        lines.values().forEach(LineRender::remove);
        lines.clear();
    }

    private record Edge(int x, int z, int dirX, int dirZ) {
        public Point computeA(BoundingBox box) {
            return box.relativeStart().add(
                    box.width() * x,
                    1,
                    box.depth() * z);
        }

        public Point computeB(BoundingBox box) {
            return computeA(box).add(
                    box.width() * dirX,
                    0,
                    box.depth() * dirZ);
        }
    }

    private record RenderedEdge(Edge edge, boolean aimingTop, boolean sub, boolean side) {}
}
