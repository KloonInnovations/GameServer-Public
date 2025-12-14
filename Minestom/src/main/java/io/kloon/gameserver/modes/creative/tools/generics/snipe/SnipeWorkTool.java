package io.kloon.gameserver.modes.creative.tools.generics.snipe;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import net.minestom.server.coordinate.BlockVec;

public interface SnipeWorkTool<ItemBound> {
    BlocksWork createWork(CreativePlayer player, ToolClick click, BlockVec targetPos, ItemBound settings);

    ChangeMeta createChangeMeta(CreativePlayer player, ToolClick click, ItemBound settings);

    void onJobComplete(CreativePlayer player, ToolClick click, ItemBound settings, Change change);
}
