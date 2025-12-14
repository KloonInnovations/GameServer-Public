package io.kloon.gameserver.modes.creative.selection.rendering;

import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.util.coordinates.CardinalDirection.*;

public class DisplayCuboid {
    private final List<DisplayEdge> edges;
    private boolean interpolation = true;

    private BoundingBox cuboid;

    private DisplayCuboid(List<DisplayEdge> edges, BoundingBox cuboid) {
        this.edges = new ArrayList<>(edges);
        this.cuboid = cuboid;
    }

    public DisplayCuboid withInterpolation(boolean interpolation) {
        this.interpolation = interpolation;
        return this;
    }

    public DisplayCuboid withMaterial(Block material) {
        edges.forEach(edge -> edge.line().editMeta(meta -> meta.setBlockState(material)));
        return this;
    }

    public DisplayCuboid withGlowColor(@Nullable Color color) {
        edges.forEach(edge -> edge.line.editMeta(meta -> {
            if (color == null) {
                meta.setHasGlowingEffect(false);
            } else {
                meta.setHasGlowingEffect(true);
                meta.setGlowColorOverride(color.asRGB());
            }
        }));
        return this;
    }

    public void spawnEdgeDebugs(Instance instance) {
        Point start = cuboid.relativeStart();
        Vec dimensions = new Vec(cuboid.width(), cuboid.height(), cuboid.depth());
        for (int i = 0; i < edges.size(); i++) {
            DisplayEdge edge = edges.get(i);
            Entity entity = new Entity(EntityType.TEXT_DISPLAY);
            TextDisplayMeta meta = (TextDisplayMeta) entity.getEntityMeta();
            meta.setText(MM."<green>\{i}");
            meta.setHasNoGravity(true);
            meta.setScale(new Vec(2, 2, 2));
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);

            Point point = start.add(dimensions.mul(edge.anchor));
            double length = dimensions.mul(edge.dir.vec()).length();
            Point entityPos = point.add(edge.dir.mul(length / 2)).add(0, 0.2, 0);

            entity.setInstance(instance, entityPos);
        }
    }

    public void editFaceEntities(CardinalDirection faceNormal, Consumer<BlockDisplayMeta> consumer) {
        for (DisplayEdge edge : edges) {
            if (edge.faceNormals.contains(faceNormal)) {
                edge.line.editMeta(consumer);
            }
        }
    }

    public void adjust(BoundingBox cuboid) {
        this.cuboid = cuboid;
        Point start = cuboid.relativeStart();
        Vec dimensions = new Vec(cuboid.width(), cuboid.height(), cuboid.depth());
        edges.forEach(edge -> {
            Point point = start.add(dimensions.mul(edge.anchor));
            double length = dimensions.mul(edge.dir.vec()).length();
            edge.line.adjust(point, length, interpolation);
        });
    }

    public void remove() {
        edges.forEach(DisplayEdge::remove);
        edges.clear();
    }

    private static final List<Edge> CUBOID_EDGES = Arrays.asList(
            new Edge(new Vec(0, 0, 0), SOUTH, Set.of(DOWN, WEST)),
            new Edge(new Vec(0, 0, 0), UP, Set.of(WEST, NORTH)),
            new Edge(new Vec(0, 0, 0), EAST, Set.of(DOWN, NORTH)),

            new Edge(new Vec(1, 1, 1), NORTH, Set.of(EAST, UP)),
            new Edge(new Vec(1, 1, 1), DOWN, Set.of(EAST, SOUTH)),
            new Edge(new Vec(1, 1, 1), WEST, Set.of(SOUTH, UP)),

            new Edge(new Vec(0, 1, 0), SOUTH, Set.of(WEST, UP)),
            new Edge(new Vec(0, 1, 0), EAST, Set.of(NORTH, UP)),

            new Edge(new Vec(1, 0, 1), NORTH, Set.of(EAST, DOWN)),
            new Edge(new Vec(1, 0, 1), WEST, Set.of(SOUTH, DOWN)),

            new Edge(new Vec(1, 0, 0), UP, Set.of(NORTH, EAST)),
            new Edge(new Vec(0, 1, 1), DOWN, Set.of(SOUTH, WEST))
    );

    public static DisplayCuboid spawn(Instance instance, BoundingBox cuboid) {
        Point start = cuboid.relativeStart();
        Vec dimensions = new Vec(cuboid.width(), cuboid.height(), cuboid.depth());
        List<DisplayEdge> edges = CUBOID_EDGES.stream().map(edge -> {
            Point point = start.add(dimensions.mul(edge.anchor));
            double length = dimensions.mul(edge.dir.vec()).length();
            ExtendoLine line = ExtendoLine.spawn(instance, point, edge.dir, length);
            return new DisplayEdge(edge.anchor, edge.dir, line, edge.faceNormals);
        }).toList();
        return new DisplayCuboid(edges, cuboid);
    }

    private record Edge(Vec anchor, CardinalDirection dir, Set<CardinalDirection> faceNormals) {}

    private record DisplayEdge(Vec anchor, CardinalDirection dir, ExtendoLine line, Set<CardinalDirection> faceNormals) {
        public void remove() {
            line.remove();
        }
    }
}
