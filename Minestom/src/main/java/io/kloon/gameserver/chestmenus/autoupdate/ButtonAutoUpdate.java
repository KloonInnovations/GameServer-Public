package io.kloon.gameserver.chestmenus.autoupdate;

import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.TaskSchedule;

import java.util.function.Supplier;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ButtonAutoUpdate implements Supplier<TaskSchedule> {
    private final ChestButton button;
    private final int slot;

    private final Player player;
    private final TaskSchedule period;

    public ButtonAutoUpdate(ChestButton button, int slot, Player player, TaskSchedule period) {
        this.button = button;
        this.slot = slot;
        this.player = player;
        this.period = period;
    }

    @Override
    public TaskSchedule get() {
        try {
            AbstractInventory inv = player.getOpenInventory();
            if (!(inv instanceof ChestMenuInv chestMenuInv)) {
                return TaskSchedule.stop();
            }
            ChestMenu menu = chestMenuInv.getMenu();

            ChestButton buttonInSlot = menu.getButton(slot);
            if (buttonInSlot != button) {
                return TaskSchedule.stop();
            }

            ItemStack renderedItem = menu.render(slot, buttonInSlot, player);
            chestMenuInv.setItemStack(slot, renderedItem);
        } catch (Throwable t) {
            player.sendMessage(MM."<red>There was an error auto-updating a button in this menu!");
            player.closeInventory();
            return TaskSchedule.stop();
        }

        return period;
    }

    public static void start(Player player, ChestButton button, int slot) {
        start(player, button, slot, TaskSchedule.tick(20));
    }

    public static void start(Player player, ChestButton button, int slot, TaskSchedule period) {
        player.scheduler().scheduleTask(new ButtonAutoUpdate(button, slot, player, period), period);
    }
}
