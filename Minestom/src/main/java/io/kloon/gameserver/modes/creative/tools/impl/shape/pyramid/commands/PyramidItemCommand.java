package io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.commands;

import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.PyramidTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.menu.PyramidToolMenu;

public class PyramidItemCommand extends ToolItemCommand<PyramidTool> {
    public PyramidItemCommand(PyramidTool tool) {
        super(tool);

        addSyntaxBlockToFillWith();
        addSyntaxNumber("steps", "number of steps", PyramidToolMenu.STEPS);
        addSyntaxNumber("steps_height", "height of each step", PyramidToolMenu.STEPS_HEIGHT);
        addSyntaxNumber("steps_length", "width and depth of each step", PyramidToolMenu.STEPS_LENGTH);
        addSyntaxToggleSetting("hollow", PyramidToolMenu.HOLLOW);
        addSyntaxToggleSetting("upside_down", PyramidToolMenu.UPSIDE_DOWN);
    }
}
