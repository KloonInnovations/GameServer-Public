package io.kloon.gameserver.modes.creative.tools.generics.selectionface;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.util.coordinates.CardinalDirection;

public interface SelectionFaceTool<Settings> {
    void handleClickFace(CreativePlayer player, ToolClick click, Settings settings, TwoCuboidSelection selection, CardinalDirection faceDir);
}
