package io.kloon.gameserver.modes.creative.tools.impl.laser;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolSidebar;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class LaserSidebar implements ToolSidebar<LaserToolSettings, LaserTool.Preferences> {
    @Override
    public Lore generate(CreativePlayer player, LaserToolSettings settings, LaserTool.Preferences preferences) {
        Lore lore = new Lore();

        lore.add(MM."<white>\{LaserTool.ICON} \{CreativeToolType.LASER.getDisplayName()}");
        lore.add(MM."<white>Mode: <title>\{settings.getMode().label()}");

        if (settings.getRadius() == 0) {
            lore.add(MM."<white>Radius: <green>Single Block");
        } else {
            lore.add(MM."<white>Radius: <green>\{settings.getRadius()} blocks around");
        }

        return lore;
    }
}
