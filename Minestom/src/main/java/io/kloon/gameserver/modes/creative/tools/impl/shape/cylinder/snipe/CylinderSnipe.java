package io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.snipe;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.CylinderTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.CylinderToolSettings;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.TargetBlockDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;
import io.kloon.gameserver.util.joml.JomlUtils;
import io.kloon.gameserver.util.rendering.LineRender;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.item.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import redis.clients.jedis.graph.entities.Edge;

import java.util.*;

public class CylinderSnipe extends ToolSnipe<CylinderToolSettings> {
    private final CylinderTool tool;

    private TargetBlockDisplay targetDisplay;

    private final Map<Vertex, Vec> vertexPositions = new HashMap<>();
    private final Map<Edge, LineRender> edges = new HashMap<>();

    private static final int VERTICALS = 8;

    public CylinderSnipe(CreativePlayer player, CylinderTool tool) {
        super(player);
        this.tool = tool;
    }

    @Override
    protected void handleTick(BlockVec target, CylinderToolSettings settings) {
        SnipeVisibility vis = player.getSnipe().getVisibility();

        if (targetDisplay == null) {
            targetDisplay = new TargetBlockDisplay(this)
                    .withMaterial(vis.editMat(Material.GREEN_STAINED_GLASS))
                    .withGlowColor(vis.editGlow(CylinderTool.COLOR));
            targetDisplay.setInstance(instance);
        }

        if (!vis.isVisible()) {
            vertexPositions.clear();
            edges.values().forEach(Entity::remove);
            edges.clear();
        }

        if (vis.isVisible()) {
            CylinderTool.Preferences preferences = tool.getPlayerBound(player);

            Vec center = target.asVec();
            double radius = settings.getRadius() + 0.5;
            boolean odd = !settings.isEven();
            boolean animate = preferences.isAnimatePreview();

            if (odd) {
                center = center.add(0.5, 0, 0.5);
                radius += 0.5;
            }

            Set<Vertex> relevantVertices = new HashSet<>();
            int horizontals = computeHorizontals(settings.getThickness());
            for (int v = 0; v < VERTICALS; ++v) {
                for (int h = 0; h < horizontals; ++h) {
                    Vertex vertex = new Vertex(h, v);
                    Vec pos = computePosition(vertex, horizontals, settings.getThickness(), center, radius, animate);
                    vertexPositions.put(vertex, pos);
                    relevantVertices.add(vertex);
                }
            }
            Set<Vertex> irrelevant = Sets.difference(vertexPositions.keySet(), relevantVertices);
            new HashSet<>(irrelevant).forEach(vertexPositions::remove);

            for (int v = 0; v < VERTICALS; ++v) {
                for (int h = 0; h < horizontals; ++h) {
                    Vertex vertex = new Vertex(h, v);
                    Vertex nextH = new Vertex((h + 1) % horizontals, v);
                    updateEdge(vis, vertex, nextH);

                    Vertex nextV = new Vertex(h, (v + 1) % VERTICALS);
                    updateEdge(vis, vertex, nextV);
                }
            }

            edges.entrySet().removeIf(entry -> {
                Edge edge = entry.getKey();
                boolean remove = !vertexPositions.containsKey(edge.a)
                                 || !vertexPositions.containsKey(edge.b);
                if (remove) {
                    entry.getValue().remove();
                }
                return remove;
            });
        }
    }

    private void updateEdge(SnipeVisibility vis, Vertex a, Vertex b) {
        Vec posA = vertexPositions.get(a);
        Vec posB = vertexPositions.get(b);

        Edge edge = new Edge(a, b);
        LineRender edgeDisplay = edges.computeIfAbsent(edge, _ -> LineRender.spawn(instance, posA, posB));

        edgeDisplay
                .withMaterial(vis.editMat(Material.GREEN_STAINED_GLASS))
                .withGlowColor(vis.editGlow(CylinderTool.COLOR))
                .adjust(posA, posB);
    }

    private int computeHorizontals(double thickness) {
        if (thickness < 7) {
            return 2;
        } if (thickness < 22) {
            return 3;
        }
        return 4;
    }

    private Vec computePosition(Vertex vertex, int horizontals, double thickness, Vec center, double radius, boolean animate) {
        double seconds = GlobalMinestomTicker.getTick() * 0.05;
        double animRad = animate
                ? seconds * Math.PI * 2 * 0.1
                : 0;

        double radY = ((double) vertex.vertical / VERTICALS) * (Math.PI * 2) + animRad + Math.PI / 8;
        double height = Math.floor(((double) vertex.horizontal / (horizontals - 1)) * thickness - thickness / 2);

        Vector3f pos = new Matrix4f()
                .rotateY((float) radY)
                .transformPosition(new Vector3f(0, (float) height, (float) radius));

        return JomlUtils.unthreef(pos).add(center);
    }

    @Override
    protected void handleRemove() {
        if (targetDisplay != null) {
            targetDisplay.remove();
        }

        vertexPositions.clear();

        edges.values().forEach(Entity::remove);
        edges.clear();
    }

    private record Vertex(int horizontal, int vertical) {}
    private record Edge(Vertex a, Vertex b) {}
}
