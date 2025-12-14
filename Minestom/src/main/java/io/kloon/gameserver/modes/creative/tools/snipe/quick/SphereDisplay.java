package io.kloon.gameserver.modes.creative.tools.snipe.quick;

import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.gameserver.util.joml.JomlUtils;
import io.kloon.gameserver.util.rendering.LineRender;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class SphereDisplay implements QuickDisplay {
    private final Instance instance;

    private final Map<Vertex, Vec> dotPositions = new HashMap<>();
    private final Map<Edge, LineRender> edges = new HashMap<>();

    private Material material = Material.BLUE_STAINED_GLASS;
    private Color glowColor = null;

    private static final int HORIZONTALS = 8; // note: this shit is wrong and inverted with verticals
    private static final int VERTICALS = 6;

    public SphereDisplay(Instance instance) {
        this.instance = instance;
    }

    @Override
    public SphereDisplay withMaterial(Material material) {
        this.material = material;
        edges.values().forEach(edge -> edge.withMaterial(material));
        return this;
    }

    @Override
    public SphereDisplay withGlowColor(@Nullable Color color) {
        this.glowColor = color;
        edges.values().forEach(edge -> edge.withGlowColor(color));
        return this;
    }

    @Override
    public void update(BlockVec target, double radius, boolean odd, boolean animate) {
        Vec center = target.asVec();

        if (odd) {
            center = center.add(0.5, 0.5, 0.5);
            radius += 0.5;
        }

        for (int v = 0; v < VERTICALS; ++v) {
            for (int h = 0; h < HORIZONTALS; ++h) {
                Vertex vertex = new Vertex(v, h);
                Vec dotPos = computePosition(vertex, center, radius, animate);
                dotPositions.put(vertex, dotPos);
            }
        }

        for (int v = 0; v < VERTICALS; ++v) {
            for (int h = 0; h < HORIZONTALS; ++h) {
                Vertex vertex = new Vertex(v, h);
                Vertex nextH = new Vertex(v, (h + 1) % HORIZONTALS);
                updateEdge(vertex, nextH);

                if (v < VERTICALS - 1) {
                    Vertex nextV = new Vertex((v + 1) % VERTICALS, h);
                    updateEdge(vertex, nextV);
                }
            }
        }
    }

    private void updateEdge(Vertex a, Vertex b) {
        Vec posA = dotPositions.get(a);
        Vec posB = dotPositions.get(b);

        Edge edge = new Edge(a, b);
        LineRender edgeDisplay = edges.computeIfAbsent(edge, _ -> {
            LineRender display = new LineRender(posA, posB)
                    .withMaterial(material)
                    .withGlowColor(glowColor);
            display.setInstance(instance);
            return display;
        });
        edgeDisplay.adjust(posA, posB);
    }

    private Vec computePosition(Vertex vertex, Vec center, double radius, boolean animate) {
        double seconds = GlobalMinestomTicker.getTick() * 0.05;
        double animRad = animate
                ? seconds * Math.PI * 2 * 0.1
                : 0;

        double radY = ((double) vertex.vertical / HORIZONTALS) * (Math.PI * 2) + animRad + Math.PI / 8;
        double radX = ((vertex.horizontal + 0.5) / VERTICALS) * (Math.PI) - Math.PI / 2;

        Vector3f pos = new Matrix4f()
                .rotateY((float) radY)
                .rotateX((float) radX)
                .transformPosition(new Vector3f(0, 0, (float) radius));

        return JomlUtils.unthreef(pos).add(center);
    }

    @Override
    public void remove() {
        dotPositions.clear();

        edges.values().forEach(Entity::remove);
        edges.clear();
    }

    private record Vertex(int horizontal, int vertical) {}
    private record Edge(Vertex a, Vertex b) {}
}
