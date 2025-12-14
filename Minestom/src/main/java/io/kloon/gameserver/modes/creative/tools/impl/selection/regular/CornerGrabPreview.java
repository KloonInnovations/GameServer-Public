package io.kloon.gameserver.modes.creative.tools.impl.selection.regular;

import io.kloon.gameserver.minestom.color.ColorUtils;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.util.ChangeTracker;
import io.kloon.gameserver.util.coordinates.BoxCorner;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class CornerGrabPreview extends Entity {
    private final CreativePlayer player;
    private final SelectionTool tool;

    public static final Material MATERIAL = Material.LIME_CONCRETE;

    private final ChangeTracker<Pos> positionTracker = new ChangeTracker<>();

    public CornerGrabPreview(CreativePlayer player, SelectionTool tool, BoundingBox cuboid) {
        super(EntityType.ITEM_DISPLAY);
        this.player = player;
        this.tool = tool;

        Color color = player.getCreativeStorage().getSelectionColors().getFullSelection();

        ItemDisplayMeta meta = (ItemDisplayMeta) getEntityMeta();

        Material material = ColorUtils.closestConcrete(color);
        meta.setItemStack(ItemStack.of(material));

        meta.setHasNoGravity(true);

        meta.setGlowColorOverride(color.asRGB());
        meta.setHasGlowingEffect(true);

        meta.setTransformationInterpolationDuration(1);

        double scale = getScale(cuboid);
        meta.setScale(new Vec(scale));

        meta.setOnFire(true);

        updateViewableRule(p -> p == player);
    }

    @Override
    public void update(long time) {
        if (!player.isHoldingTool(CreativeToolType.SELECTION)) {
            remove();
            return;
        }

        BoxCorner hoveredCorner = tool.computeHoveredCorner(player);
        if (hoveredCorner == null) {
            remove();
            return;
        }

        if (!(player.getSelection() instanceof TwoCuboidSelection sel)) {
            return;
        }
        BoundingBox cuboid = sel.getCuboid();

        Vec positionVec = hoveredCorner.onBox(cuboid);
        positionTracker.acceptIfChanged(Pos.fromPoint(positionVec), this::teleport);
    }

    private static double getScale(BoundingBox cuboid) {
        if (BoundingBoxUtils.volumeRounded(cuboid) == 1) {
            return 0.25;
        } else {
            return 0.45;
        }
    }
}
