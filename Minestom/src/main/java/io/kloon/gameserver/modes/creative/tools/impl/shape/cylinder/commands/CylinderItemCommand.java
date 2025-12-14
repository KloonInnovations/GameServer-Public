package io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.commands;

import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.CylinderTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.menu.CylinderToolMenu;

public class CylinderItemCommand extends ToolItemCommand<CylinderTool> {
    public CylinderItemCommand(CylinderTool tool) {
        super(tool);

        addSyntaxBlockToFillWith();

        addSyntaxNumber("radius", "cylinder radius", CylinderToolMenu.RADIUS);
        addSyntaxNumber("thickness", "cylinder height", CylinderToolMenu.THICKNESS);
        addSyntaxToggleSetting("centered", CylinderToolMenu.EVEN);
        addSyntaxToggleSetting("hollow", CylinderToolMenu.HOLLOW);
    }
}
