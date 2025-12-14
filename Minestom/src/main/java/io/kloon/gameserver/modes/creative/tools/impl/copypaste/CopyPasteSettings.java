package io.kloon.gameserver.modes.creative.tools.impl.copypaste;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.datainworld.minestom.StorageVec;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClip;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipFlip;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipRotation;
import net.minestom.server.coordinate.Vec;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

public class CopyPasteSettings {
    private ObjectId clipId = null;
    private StorageVec grabVec;

    private String rotationDbKey = ClipRotation.ZERO.getDbKey();
    private boolean flipX = false;
    private boolean flipZ = false;

    private boolean ignorePasteAir = true;
    private boolean ignoreMasks = true;

    private boolean transformProperties = true;

    @Nullable
    public ObjectId getClipId() {
        return clipId;
    }

    @Nullable
    public WorldClip getClip(CreativePlayer player) {
        if (clipId == null) return null;
        return player.getClipboard().getClip(clipId);
    }

    public void setClip(WorldClip clip) {
        this.clipId = clip.id();
        this.grabVec = null;
    }

    public ClipRotation getRotation() {
        return ClipRotation.BY_DB_KEY.get(rotationDbKey, ClipRotation.ZERO);
    }

    public void setRotation(ClipRotation rotation) {
        this.rotationDbKey = rotation.getDbKey();
    }

    public Vec getGrabWithinBb() {
        if (grabVec == null) return Vec.ZERO;
        return grabVec.toVec();
    }

    public void setGrabWithinBb(Vec grabVec) {
        this.grabVec = new StorageVec(grabVec);
    }

    public boolean isFlipX() {
        return flipX;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public boolean isFlipZ() {
        return flipZ;
    }

    public void setFlipZ(boolean flipZ) {
        this.flipZ = flipZ;
    }

    public ClipFlip getFlip() {
        return new ClipFlip(flipX, false, flipZ);
    }

    public boolean isIgnorePasteAir() {
        return ignorePasteAir;
    }

    public void setIgnorePasteAir(boolean ignorePasteAir) {
        this.ignorePasteAir = ignorePasteAir;
    }

    public boolean isIgnoreMasks() {
        return ignoreMasks;
    }

    public void setIgnoreMasks(boolean ignoreMasks) {
        this.ignoreMasks = ignoreMasks;
    }

    public boolean isTransformingProperties() {
        return transformProperties;
    }

    public void setTransformProperties(boolean transformProperties) {
        this.transformProperties = transformProperties;
    }
}
