package io.kloon.gameserver.modes.creative.tools.generics.selectionface;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.generics.ToolMessages;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import io.kloon.gameserver.util.physics.Collisions;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SelectionFaceHandler<ItemBound, Tool extends CreativeTool<ItemBound, ?> & SelectionFaceTool<ItemBound>> {
    private final Tool tool;

    public SelectionFaceHandler(Tool tool) {
        this.tool = tool;
    }

    public void handleUse(CreativePlayer player, ToolClick click) {
        if (!(player.getSelection() instanceof TwoCuboidSelection selection)) {
            ToolMessages.sendRequireSelection(player);
            return;
        }

        BoundingBox originCuboid = selection.getCuboid();

        Vec faceNormalVec = Collisions.raycastBoxGetFaceNormal(player.getEyeRay(), originCuboid);
        if (faceNormalVec == null) {
            player.sendPit(NamedTextColor.RED, "WHAT?", MM."<gray>Click on a selection to use this tool!");
            player.playSound(SoundEvent.ENTITY_BEE_HURT, 0.8);
            return;
        }

        CardinalDirection faceNormalDir = CardinalDirection.fromVec(faceNormalVec);
        ItemBound settings = tool.getItemBound(click.getItem());

        tool.handleClickFace(player, click, settings, selection, faceNormalDir);
    }
}
