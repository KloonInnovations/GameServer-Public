package io.kloon.gameserver.modes.creative.tools.impl.teleport;

import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.util.physics.Collisions;
import io.kloon.gameserver.util.ChangeTracker;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class TeleportPreview extends Entity {
    private final CreativePlayer player;
    private final TeleportTool tool;

    public static final Color COLOR = new Color(170, 0, 170);
    private final ChangeTracker<Quaternionf> rotationTrack = new ChangeTracker<>();

    public TeleportPreview(CreativePlayer player, TeleportTool tool) {
        super(EntityType.ITEM_DISPLAY);
        this.player = player;
        this.tool = tool;

        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        meta.setItemStack(ItemStack.of(Material.PURPLE_STAINED_GLASS_PANE));
        meta.setHasNoGravity(true);
        meta.setGlowColorOverride(COLOR.asRGB());
        meta.setHasGlowingEffect(true);
        meta.setScale(new Vec(0.6, 0.6, 0.6));
        meta.setPosRotInterpolationDuration(1);

        updateViewableRule(p -> p == player);
    }

    @Override
    public void update(long time) {
        ItemStack item = player.getItemInMainHand();
        if (!tool.isTool(item)) {
            remove();
            return;
        }

        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();
        Position position = computePosition();
        teleport(position.pos);

        if (getAliveTicks() % 3 == 0) {
            //player.sendPacket(new ParticlePacket(Particle.FALLING_OBSIDIAN_TEAR, true, position.pos, new Vec(0.1, 0.1, 0.1), 0.02f, 4));
        }

        Quaternionf q = position.rotation.get(new Quaternionf());
        meta.setRightRotation(new float[]{q.x, q.y, q.z, q.w});
    }

    public Position computePosition() {
        Point targetBlock = tool.getTargetBlockNotNull(player);
        Pos pos = new Pos(targetBlock.add(0.5, 0.5, 0.5), 0, 0);
        if (player.getInstance().getBlock(targetBlock).isAir()) {
            return new Position(pos, new AxisAngle4f(0, 0, 0, 0));
        }

        BoundingBox boundingBox = BoundingBoxUtils.fromBlock(targetBlock);
        Vec faceNormal = Collisions.raycastBoxGetFaceNormal(player.getEyeRay(), boundingBox);
        if (faceNormal == null) {
            return new Position(pos, new AxisAngle4f(0, 0, 0, 0));
        }

        CardinalDirection direction = CardinalDirection.fromVec(faceNormal);
        AxisAngle4f rotation;
        if (direction.vertical()) {
            rotation = new AxisAngle4f((float) Math.PI / 2, 1, 0, 0);
        } else if (direction.x() == 0) {
            rotation = new AxisAngle4f(0, 0, 0, 0);
        } else {
            rotation = new AxisAngle4f((float) Math.PI / 2, 0, 1, 0);
        }

        pos = pos.add(faceNormal.mul(0.5));
        return new Position(pos, rotation);
    }

    public record Position(
            Pos pos,
            AxisAngle4f rotation
    ) {
    }
}
