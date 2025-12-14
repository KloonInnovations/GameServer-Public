package io.kloon.gameserver.modes.creative.menu.preferences.common.snipeshape;

import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.snipe.shape.SnipeShape;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class SnipeShapeButton extends CycleButton<SnipeShape> {
    public SnipeShapeButton(int slot, CreativeTool<?, ? extends SnipeShapePref> tool, CreativePlayer player) {
        super(new Cycle<>(SnipeShape.LIST_WITHOUT_NONE), slot);
        withIcon(Material.MELON);
        withTitle(MM."<title>Selection Shape");
        withDescription(MM_WRAP."<gray>The shape of the selection area.");
        withOnClick((p, shape) -> tool.editPlayerBound((CreativePlayer) p, pref -> pref.setSnipeShape(shape)));

        SnipeShape snipeShape = tool.getPlayerBound(player).getSnipeShape();
        cycle.select(snipeShape);
    }
}
