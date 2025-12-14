package io.kloon.gameserver.modes.creative.tools.impl.fill.commands;

import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.fill.FillTool;

public class FillItemCommand extends ToolItemCommand<FillTool> {
    public FillItemCommand(FillTool tool) {
        super(tool);
        addSyntaxBlockToFillWith();
    }
}
