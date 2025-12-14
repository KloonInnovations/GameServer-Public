package io.kloon.gameserver.modes.creative.menu;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.creative.CreativeWorldsMenu;
import io.kloon.gameserver.creative.CreativeWorldsMenuProxy;
import io.kloon.gameserver.modes.creative.CreativeMode;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.clipboard.ClipboardMenu;
import io.kloon.gameserver.modes.creative.menu.enderchest.EnderChestMenu;
import io.kloon.gameserver.modes.creative.menu.history.HistoryMenu;
import io.kloon.gameserver.modes.creative.menu.jobs.JobsManagementMenu;
import io.kloon.gameserver.modes.creative.menu.masks.MasksSelectionMenu;
import io.kloon.gameserver.modes.creative.menu.patterns.PatternSelectionProxy;
import io.kloon.gameserver.modes.creative.menu.preferences.CreativePreferencesMenu;
import io.kloon.gameserver.modes.creative.menu.random.BackToHubButton;
import io.kloon.gameserver.modes.creative.menu.random.ClearInventoryButton;
import io.kloon.gameserver.modes.creative.menu.tools.ToolsSelectionMenu;
import io.kloon.gameserver.modes.creative.menu.worldadmin.WorldAdminMenu;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.WaypointsManagementMenu;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreativeMainMenu extends ChestMenu {
    public static final String ICON = "\uD83C\uDF0C"; // 🌌

    private final CreativePlayer player;
    private final CreativeMode creative;

    private final CreativePreferencesMenu settingsMenu;

    public CreativeMainMenu(CreativePlayer player) {
        super(STR."\{ICON} Creative Menu");
        this.player = player;
        this.creative = player.getCreative();

        this.settingsMenu = new CreativePreferencesMenu(this, true);

        CreativeWorldsMenu.GET_PLAYER_WORLD = p -> {
            if (!(p instanceof CreativePlayer cp)) return null;
            return cp.getInstance().getWorldDef();
        };

        setTitleFunction(_ -> {
            if (player.canEditWorld()) {
                return MM."\{ICON} Creative Menu";
            } else {
                return MM."\{ICON} Creative Menu (Visitor)";
            }
        });
    }

    public CreativePlayer getPlayer() {
        return player;
    }

    public CreativePreferencesMenu getSettingsMenu() {
        return settingsMenu;
    }

    @Override
    protected void registerButtons() {
        if (player.canEditWorld()) {
            reg(10, new ToolsSelectionMenu(player, this, creative.getToolsListener()));
            reg(12, new MasksSelectionMenu(this));
            reg(14, new PatternSelectionProxy(this));

            reg(16, new HistoryMenu(creative.getHistoryTool(), this, player));

            reg(28, new EnderChestMenu(this, player));
            reg(30, new ClipboardMenu(this, creative.getToolsListener()));
            reg(32, new WorldAdminMenu(this));
            reg(34, new JobsManagementMenu(player.getInstance().getJobQueue(), this));

            reg(45, new ClearInventoryButton());
            reg(46, new WaypointsManagementMenu(this, player));

        } else {
            reg(21, new ToolsSelectionMenu(player, this, creative.getToolsListener()));
            reg(23, new ClearInventoryButton());
        }

        reg(size.last() - 2, settingsMenu);
        reg(size.last() - 1, new CreativeWorldsMenuProxy(this, Kgs.INSTANCE.getWorldListsCache()));
        reg(size.last(), new BackToHubButton());
    }
}
