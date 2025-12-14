package io.kloon.gameserver.chestmenus.autoupdate;

import net.minestom.server.timer.TaskSchedule;

public interface AutoUpdateMenu {
    default boolean shouldReloadMenu() {
        return true;
    }

    default TaskSchedule getAutoUpdatePeriod() {
        return TaskSchedule.tick(10);
    }}
