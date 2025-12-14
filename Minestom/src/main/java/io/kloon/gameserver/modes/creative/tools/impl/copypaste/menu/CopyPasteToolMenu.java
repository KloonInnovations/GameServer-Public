package io.kloon.gameserver.modes.creative.tools.impl.copypaste.menu;

import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.PlayerClipboard;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteSettings;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.menu.elems.ClipboardSlotButton;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.menu.elems.FlipClipButton;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.menu.elems.RotateClipButton;
import io.kloon.gameserver.modes.creative.tools.menus.*;
import io.kloon.gameserver.modes.creative.tools.menus.commands.ToolCommandsInfo;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolSettingToggleButton;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class CopyPasteToolMenu extends CreativeToolMenu<CopyPasteTool> {
    public static final ToolToggle<CopyPasteSettings> IGNORE_PASTING_AIR = new ToolToggle<>(
            Material.FEATHER, "Ignore Pasting Air",
            MM_WRAP."<gray>When enabled, air blocks from your clip will be ignored when pasting.\n\n<dark_gray>This allows blending your clip into the world more easily.",
            CopyPasteSettings::isIgnorePasteAir, CopyPasteSettings::setIgnorePasteAir);

    public static final ToolToggle<CopyPasteSettings> IGNORE_MASKS = new ToolToggle<>(
            Material.DRAGON_HEAD, "Ignore Masks",
            MM_WRAP."<gray>Ignore the masks you're wearing while pasting.",
            CopyPasteSettings::isIgnoreMasks, CopyPasteSettings::setIgnoreMasks);

    public static final ToolToggle<CopyPasteSettings> TRANSFORM_PROPERTIES = new ToolToggle<>(
            Material.JIGSAW, "Transform Properties",
            MM_WRAP."<gray>Rotate and flip block properties, for example the direction that stairs are facing. <red>This feature is in development!",
            CopyPasteSettings::isTransformingProperties, CopyPasteSettings::setTransformProperties);

    public CopyPasteToolMenu(CopyPasteTool tool, ItemRef itemRef) {
        super(tool, itemRef);
    }

    @Override
    protected void registerButtons() {
        for (int index = 0; index < PlayerClipboard.CLIPBOARD_ENTRIES; ++index) {
            int slot = 11 + index;
            reg(slot, new ClipboardSlotButton(index, slot));
        }

        reg(29, slot -> new FlipClipButton(tool, itemRef, Axis.X, CopyPasteSettings::isFlipX, CopyPasteSettings::setFlipX, slot));
        reg(30, slot -> new FlipClipButton(tool, itemRef, Axis.Z, CopyPasteSettings::isFlipZ, CopyPasteSettings::setFlipZ, slot));

        reg(38, new RotateClipButton(tool, itemRef, false));
        reg(39, new RotateClipButton(tool, itemRef, true));

        reg(32, slot -> new ToolSettingToggleButton<>(slot, tool, itemRef, IGNORE_PASTING_AIR));
        reg(41, slot -> new ToolSettingToggleButton<>(slot, tool, itemRef, TRANSFORM_PROPERTIES));
        reg(42, slot -> new ToolSettingToggleButton<>(slot, tool, itemRef, IGNORE_MASKS));

        reg().toolCommands(this);
    }
}
