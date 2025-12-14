package io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.commands;

import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.SphereTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.menu.SphereToolMenu;

public class SphereItemCommand extends ToolItemCommand<SphereTool> {
    public SphereItemCommand(SphereTool tool) {
        super(tool);

        addSyntaxBlockToFillWith();

        addSyntaxNumber("radius", "sphere radius", SphereToolMenu.SPHERE_RADIUS);
        addSyntaxToggleSetting("hollow", SphereToolMenu.HOLLOW);
        addSyntaxToggleSetting("centered", SphereToolMenu.CENTERED);
    }
}
