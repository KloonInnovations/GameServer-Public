package io.kloon.gameserver.modes.creative.selection.rendering;

import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ExtendoLine {
    public static final double PARTS_THICKNESS = 1.0 / 16;
    private static final double MAX_PART_LENGTH = 3 * 16;

    private final Instance instance;
    private final CardinalDirection direction;
    private final List<Entity> displayEntities;

    public static final Color DEFAULT_GLOW_COLOR = new Color(249, 247, 246);

    private ExtendoLine(Instance instance, CardinalDirection direction, List<Entity> displayEntities) {
        this.instance = instance;
        this.direction = direction;
        this.displayEntities = new ArrayList<>(displayEntities);
    }

    public void adjust(Point start, double length, boolean interpolate) {
        List<Part> parts = computeParts(start, direction, length);
        if (parts.size() < displayEntities.size()) {
            List<Entity> toRemove = displayEntities.subList(parts.size(), displayEntities.size());
            toRemove.forEach(Entity::remove);
            toRemove.clear();
        }

        int missing = parts.size() - displayEntities.size();
        IntStream.range(0, missing).mapToObj(_ -> {
            Entity entity = createEntity();
            entity.setInstance(instance);
            return entity;
        }).forEach(displayEntities::add);

        for (int i = 0; i < parts.size(); ++i) {
            Part part = parts.get(i);
            Entity entity = displayEntities.get(i);
            BlockDisplayMeta meta = (BlockDisplayMeta) entity.getEntityMeta();
            meta.setNotifyAboutChanges(false);
            meta.setScale(part.scale);
            if (interpolate) {
                meta.setTransformationInterpolationStartDelta(0);
                meta.setPosRotInterpolationDuration(1);
                meta.setTransformationInterpolationDuration(1);
            } else {
                meta.setPosRotInterpolationDuration(0);
                meta.setTransformationInterpolationDuration(0);
            }
            meta.setNotifyAboutChanges(true);
            entity.teleport(part.startPos.asPosition());
        }
    }

    public void remove() {
        displayEntities.forEach(Entity::remove);
        displayEntities.clear();
    }

    public void editMeta(Consumer<BlockDisplayMeta> editor) {
        displayEntities.forEach(entity -> {
            BlockDisplayMeta meta = (BlockDisplayMeta) entity.getEntityMeta();
            meta.setNotifyAboutChanges(false);
            try {
                editor.accept(meta);
            } finally {
                meta.setNotifyAboutChanges(true);
            }
        });
    }

    private static List<Part> computeParts(Point start, CardinalDirection direction, double length) {
        int partsCount = (int) Math.ceil(length / MAX_PART_LENGTH);
        List<Part> parts = new ArrayList<>(partsCount);
        for (int i = 0; i < partsCount; ++i) {
            boolean isLastPart = i == partsCount - 1;
            double partLength = isLastPart
                    ? length % MAX_PART_LENGTH
                    : MAX_PART_LENGTH;
            Vec partScale = direction.zerosAsOne().mul(PARTS_THICKNESS)
                    .add(direction.mul(partLength));
            Vec partStart = direction.mul(i * MAX_PART_LENGTH)
                    .add(start)
                    .sub(direction.zerosAsOne().mul(PARTS_THICKNESS / 2));
            parts.add(new Part(partStart, partScale));
        }
        return parts;
    }

    public static ExtendoLine spawn(Instance instance, Point start, CardinalDirection direction, double length) {
        List<Part> parts = computeParts(start, direction, length);
        List<Entity> entities = parts.stream().map(part -> {
            Entity entity = createEntity();
            BlockDisplayMeta meta = (BlockDisplayMeta) entity.getEntityMeta();
            meta.setScale(part.scale);
            meta.setTransformationInterpolationStartDelta(0);

            entity.setInstance(instance, part.startPos);
            return entity;
        }).toList();
        return new ExtendoLine(instance, direction, entities);
    }

    private static Entity createEntity() {
        Entity entity = new Entity(EntityType.BLOCK_DISPLAY);
        entity.setNoGravity(true);

        BlockDisplayMeta meta = (BlockDisplayMeta) entity.getEntityMeta();
        meta.setBlockState(Block.GRAY_CONCRETE);

        meta.setHasGlowingEffect(true);
        meta.setGlowColorOverride(DEFAULT_GLOW_COLOR.asRGB());

        return entity;
    }

    private record Part(Vec startPos, Vec scale) {}
}
