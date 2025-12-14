package io.kloon.gameserver.modes.creative.tools.impl.move.menu;

import io.kloon.gameserver.modes.creative.tools.impl.move.MoveTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class MoveToolMenu extends CreativeToolMenu<MoveTool> {
    public static final ToolToggle<MoveTool.Settings> IGNORE_AIR = new ToolToggle<>(
            Material.FEATHER, "Ignore Pasting Air",
            MM_WRAP."<gray>If enabled, air blocks from the initial selection will not be pasted over the destination.",
            MoveTool.Settings::isIgnorePasteAir, MoveTool.Settings::setIgnorePasteAir);

    public static final ToolToggle<MoveTool.Settings> IGNORE_MASKS = new ToolToggle<>(
            Material.DRAGON_HEAD, "Ignore Masks",
            MM_WRAP."<gray>Ignore the masks you're wearing while moving the blocks.",
            MoveTool.Settings::isIgnoreMasks, MoveTool.Settings::setIgnoreMasks);

    public static final ToolToggle<MoveTool.Settings> CUT = new ToolToggle<>(
            Material.SHEARS, "Cut",
            MM_WRAP."<gray>If enabled, replace the original selection with air.",
            MoveTool.Settings::isCut, MoveTool.Settings::setCut);

    public MoveToolMenu(MoveTool tool, ItemRef itemRef) {
        super(tool, itemRef);
    }

    @Override
    protected void registerButtons() {
        reg(11, slot -> new MoveDistanceButton(slot, this));

        regSettingToggle(15, CUT);

        regSettingToggle(30, IGNORE_AIR);
        regSettingToggle(32, IGNORE_MASKS);

        reg().toolCommands(this);
    }
}
