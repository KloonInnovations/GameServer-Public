package io.kloon.gameserver.modes.creative.tools.impl.copypaste.selection;

import com.google.common.base.Objects;
import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.jobs.work.pasting.Pasting;
import io.kloon.gameserver.modes.creative.selection.rendering.DisplayCuboid;
import io.kloon.gameserver.modes.creative.storage.datainworld.PasteSelectionStorage;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClip;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteSettings;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipFlip;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipRotation;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.Cuboid;
import io.kloon.gameserver.util.joml.JomlUtils;
import io.kloon.gameserver.util.physics.Collisions;
import io.kloon.gameserver.util.physics.Ray;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class PastingSelection implements PasteSelection {
    private final CreativePlayer player;
    private final WorldClip clip;

    private DisplayCuboid displayCuboid;
    private Vec aimedVec;
    private Cuboid pre;
    private Cuboid rendered;

    private DisplayCuboid originCuboid;

    private ClipRotation rotation = ClipRotation.ZERO;
    private ClipFlip flip = new ClipFlip(false, false, false);
    private boolean ignorePasteAir;
    private boolean ignoreMasks;
    private boolean transformProperties;

    private Vec lastGrabWithinBb;
    private Vec grabWithinBb;

    int keepaliveTick = 0;

    public PastingSelection(CreativePlayer player, WorldClip clip) {
        this.player = player;
        this.clip = clip;
        this.keepaliveTick = GlobalMinestomTicker.getTick();
    }

    public WorldClip getClip() {
        return clip;
    }

    public Pasting toPasting() {
        return new Pasting(
                clip.volume(),
                player.computeMaskLookup(),
                pre.a(),
                aimedVec,
                rotation,
                flip,
                ignorePasteAir,
                ignoreMasks,
                transformProperties
        );
    }

    public int getTicksUntilDisappear() {
        int ticksShowing = 15 * 20;
        int elapsed = GlobalMinestomTicker.getTick() - keepaliveTick;
        return ticksShowing - elapsed;
    }

    @Override
    public void tickHolding(CopyPasteSettings settings) {
        this.keepaliveTick = GlobalMinestomTicker.getTick();

        this.rotation = settings.getRotation();
        this.flip = settings.getFlip();
        this.ignorePasteAir = settings.isIgnorePasteAir();
        this.ignoreMasks = settings.isIgnoreMasks();
        this.transformProperties = settings.isTransformingProperties();

        if (grabWithinBb == null) {
            grabWithinBb = settings.getGrabWithinBb();
        } else {
            settings.setGrabWithinBb(grabWithinBb);
        }

        updatePastingDisplay(settings.getRotation());
        updateOriginDisplay(true);
    }

    private void updatePastingDisplay(ClipRotation rotation) {
        aimedVec = computeAimedVec();

        Vec dimensions = BoundingBoxUtils.dimensions(clip.volume().toCuboid());

        Vec a = aimedVec.sub(grabWithinBb);
        this.pre = new Cuboid(a, a.add(dimensions));
        this.rendered = rotation.rotated(aimedVec, pre);

        if (displayCuboid == null) {
            displayCuboid = DisplayCuboid.spawn(player.getInstance(), rendered.toBoundingBox())
                    .withGlowColor(player.getCreativeStorage().getSelectionColors().getNoSelection());
        } else {
            displayCuboid.withInterpolation(false).adjust(rendered.toBoundingBox());
        }
    }

    @Override
    public void tickNotHolding() {
        if (getTicksUntilDisappear() <= 0) {
            remove();
            return;
        }

        Vec targetWithinBb = getTargetBlockWithinBoundingBox(player, rendered.toBoundingBox());
        if (targetWithinBb != null) {
            Vec offsetWithinBb = targetWithinBb.sub(rendered.a());
            Matrix4f mat = rotation.rotatedMatrixAroundOrigin(Vec.ZERO, true);
            Vector3f grabWithinBbf = mat.transformPosition(JomlUtils.threef(offsetWithinBb));
            this.lastGrabWithinBb = grabWithinBb;
            this.grabWithinBb = JomlUtils.unthreef(grabWithinBbf).apply(Vec.Operator.FLOOR);
            if (!Objects.equal(lastGrabWithinBb, grabWithinBb)) {
                double pitch = 1.2 + (Math.abs(grabWithinBb.hashCode()) / 100.0) % 0.8;
                player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_CHIME, pitch, 0.4);
            }

            double range = player.getEyePosition().distance(targetWithinBb.add(0.5, 0.5, 0.5));
            player.getCreativeStorage().setPastingRange(range);

            updateOriginDisplay(false);
        }
    }

    private void updateOriginDisplay(boolean holding) {
        Vec origin;
        if (holding) {
            origin = computeAimedVec();
        } else {
            origin = rotation.rotated(aimedVec, pre.a().add(grabWithinBb));
        }

        Cuboid originCube = new Cuboid(origin, origin.add(1, 1, 1));
        originCube = rotation.rotated(origin, originCube);
        BoundingBox originBb = originCube.toBoundingBox();

        if (originCuboid == null) {
            originCuboid = DisplayCuboid.spawn(player.getInstance(), originBb)
                    .withGlowColor(player.getCreativeStorage().getSelectionColors().getFullSelection());
        } else {
            originCuboid.withInterpolation(false).adjust(originBb);
        }
    }

    private Vec computeAimedVec() {
        Vec eyePos = player.getEyePosition().asVec();
        Vec eyeDir = player.getLookVec();
        Vec ahead = eyePos.add(eyeDir.mul(player.getPastingRange()));
        return ahead.apply(Vec.Operator.FLOOR).add(rotation.floorOffset());
    }

    @Nullable
    private Vec getTargetBlockWithinBoundingBox(CreativePlayer player, BoundingBox bb) {
        if (bb == null) return null;
        Ray ray = player.getEyeRay();
        Vec collisionPoint = Collisions.raycastBoxGetPoint(ray, bb);
        if (collisionPoint == null) return null;
        return collisionPoint.add(ray.dir().mul(0.01));
    }

    @Override
    public void remove() {
        if (displayCuboid != null) {
            displayCuboid.remove();
        }
        if (originCuboid != null) {
            originCuboid.remove();
        }
        player.setPasteSelection(new NoPasteSelection(player));
    }

    @Override
    public PasteSelectionStorage toStorage() {
        return new PasteSelectionStorage();
    }
}
