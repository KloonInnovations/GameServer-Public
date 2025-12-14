package io.kloon.gameserver.chestmenus.util;

import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.CancelButton;
import io.kloon.gameserver.chestmenus.builtin.GoBackButton;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.commands.ToolCommandsInfo;

public final class CommonReg {
    private final ChestMenu menu;
    private final RegFunc regFunc;

    public CommonReg(ChestMenu menu, RegFunc regFunc) {
        this.menu = menu;
        this.regFunc = regFunc;
    }

    public CommonReg goBack(ChestMenu parent) {
        regFunc.reg(menu.size().bottomCenter(), new GoBackButton(parent));
        return this;
    }

    public CommonReg cancel(ChestMenu parent) {
        regFunc.reg(menu.size().bottomCenter() - 1, new CancelButton(parent));
        return this;
    }

    public CommonReg breadcrumbs() {
        regFunc.reg(4, menu.getBreadcrumbs());
        return this;
    }

    public CommonReg toolCommands(CreativeToolMenu<?> toolMenu) {
        regFunc.reg(menu.size().last(), new ToolCommandsInfo(toolMenu));
        return this;
    }

    public interface RegFunc {
        void reg(int slot, ChestButton button);
    }
}
