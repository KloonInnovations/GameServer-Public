package io.kloon.gameserver.modes.creative.tools.impl.history;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.history.HistoryCommand;
import io.kloon.gameserver.modes.creative.commands.history.RedoCommand;
import io.kloon.gameserver.modes.creative.commands.history.UndoCommand;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolSidebar;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.ToolClickSide;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.history.HistoryTool.*;

public class HistoryTool extends CreativeTool<Settings, Preferences> {
    public HistoryTool() {
        super(CreativeToolType.HISTORY, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        if (click.side() == ToolClickSide.LEFT) {
            UndoCommand.undo(player);
        } else {
            RedoCommand.redo(player);
        }
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>UNDO");
        lore.addAll(MM_WRAP."<gray>Undoes your latest action.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>REDO");
        lore.addAll(MM_WRAP."<gray>If you undid something, you can undo undoing it.");
    }

    @Override
    public @Nullable ToolSidebar<Settings, Preferences> createSidebar() {
        return new HistorySidebar();
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        HistoryCommand.openHistoryMenu(player);
    }

    public static class Settings {}

    public static class Preferences {}
}
