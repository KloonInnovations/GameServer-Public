package io.kloon.gameserver.modes.creative.tools.impl.teleport.commands;

import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.TeleportTool;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.menu.TeleportRangeButton;

public class TeleportItemCommand extends ToolItemCommand<TeleportTool> {
    public TeleportItemCommand(TeleportTool tool) {
        super(tool);

        addSyntaxNumber("range", "teleport range", TeleportRangeButton.RANGE);
    }
}
