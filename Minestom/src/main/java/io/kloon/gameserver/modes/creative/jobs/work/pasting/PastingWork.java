package io.kloon.gameserver.modes.creative.jobs.work.pasting;

import io.kloon.gameserver.minestom.blockchange.MultiBlockChange;
import io.kloon.gameserver.minestom.blocks.KloonPlacementRules;
import io.kloon.gameserver.minestom.blocks.transforms.BlockTransformer;
import io.kloon.gameserver.minestom.blocks.transforms.FlipTransform;
import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import io.kloon.gameserver.modes.creative.storage.blockvolume.iterator.VolumeIterator;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipFlip;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipRotation;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import org.joml.Matrix4f;

import java.util.Set;
import java.util.function.BooleanSupplier;

import static io.kloon.gameserver.util.joml.JomlUtils.threef;
import static io.kloon.gameserver.util.joml.JomlUtils.unthreef;

public class PastingWork implements BlocksWork {
    private final CreativeInstance instance;
    private final Pasting settings;

    private final BlockVolume volume;
    private final Vec dimensions;

    private final VolumeIterator iterator;
    private int iteratedBlocks = 0;

    private final Vec start;
    private final Vec cuboidStart;

    private final ClipRotation rotation;
    private final Vec rotationPivot;

    private final ClipFlip flip;

    private final BlockVolumeBuilder before = new BlockVolumeBuilder();
    private final BlockVolumeBuilder after = new BlockVolumeBuilder();

    private boolean outOfBounds = false;

    public PastingWork(CreativeInstance instance, Pasting pasting) {
        this.instance = instance;
        this.settings = pasting;
        this.volume = pasting.volume();
        this.dimensions = BoundingBoxUtils.dimensions(volume.toCuboid());
        this.start = Vec.fromPoint(pasting.start());

        this.rotationPivot = pasting.rotationPivot();
        this.rotation = pasting.rotation();

        this.flip = pasting.flip();

        this.iterator = volume.iterator();

        this.cuboidStart = Vec.fromPoint(volume.toCuboid().relativeStart());
    }

    @Override
    public boolean work(BooleanSupplier greenFlag) {
        MultiBlockChange multi = new MultiBlockChange(instance);
        try {
            RotationTransform rotTransform = rotation.toTransform();
            Set<FlipTransform> flipTransforms = flip.toTransforms();

            Matrix4f matrix = rotation.rotatedMatrixAroundOrigin(rotationPivot, false);
            while (iterator.hasNext()) {
                if (!greenFlag.getAsBoolean()) return false;
                iterator.next((x, y, z, block) -> {
                    ++iteratedBlocks;
                    if (settings.ignorePasteAir() && block.isAir()) {
                        return;
                    }

                    Vec pointInVolume = new Vec(x, y, z).sub(cuboidStart);
                    pointInVolume = flipPosition(pointInVolume);
                    Vec pointInWorld = pointInVolume.add(start).add(0.5, 0.5, 0.5);
                    pointInWorld = unthreef(matrix.transformPosition(threef(pointInWorld)));

                    if (instance.isOutOfBounds(pointInWorld)) {
                        outOfBounds = true;
                        return;
                    }

                    if (!settings.ignoreMasks() && settings.mask().isIgnored(instance, pointInWorld, instance.getBlock(pointInWorld))) {
                        return;
                    }

                    if (settings.transformProperties()) {
                        BlockTransformer transformer = KloonPlacementRules.getTransformer(block);
                        if (transformer != null) {
                            for (FlipTransform flipTransform : flipTransforms) {
                                block = transformer.flip(block, flipTransform);
                            }
                            block = transformer.rotate(block, rotTransform);
                        }
                    }

                    before.set(pointInWorld, instance);
                    after.set(pointInWorld, block);

                    multi.set(pointInWorld, block);
                });
            }
            return true;
        } finally {
            multi.applyAndBroadcast();
        }
    }

    private Vec flipPosition(Vec pointInVolume) {
        if (flip.none()) {
            return pointInVolume;
        }

        double x = flip.x()
                ? dimensions.x() - 1 - pointInVolume.x()
                : pointInVolume.x();
        double z = flip.z()
                ? dimensions.z() - 1 - pointInVolume.z()
                : pointInVolume.z();

        return new Vec(x, pointInVolume.y(), z);
    }

    @Override
    public int getPlacedSoFar() {
        return iteratedBlocks;
    }

    @Override
    public int getTotalToPlace() {
        return volume.count();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return volume.toCuboid().withOffset(start); // this is a bug because it's not being rotated
    }

    @Override
    public boolean hadOutOfBounds() {
        return outOfBounds;
    }

    @Override
    public Change getChange() {
        return new ApplyVolumeChange(before.build(), after.build());
    }
}
