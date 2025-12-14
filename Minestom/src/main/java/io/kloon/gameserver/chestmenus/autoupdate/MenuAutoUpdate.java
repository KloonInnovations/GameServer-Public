package io.kloon.gameserver.chestmenus.autoupdate;

import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Supplier;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MenuAutoUpdate implements Supplier<TaskSchedule> {
    private static final Logger LOG = LoggerFactory.getLogger(MenuAutoUpdate.class);
    
    private final ChestMenuInv inventory;
    private final ChestMenu menu;
    private final Map<Integer, ChestButton> buttons;

    private final Player player;
    private final TaskSchedule period;

    public MenuAutoUpdate(ChestMenuInv inventory, Map<Integer, ChestButton> buttons, Player player, TaskSchedule period) {
        this.inventory = inventory;
        this.menu = inventory.getMenu();
        this.buttons = buttons;
        this.player = player;
        this.period = period;
    }

    @Override
    public TaskSchedule get() {
        try {
            if (player.getOpenInventory() != inventory) {
                return TaskSchedule.stop();
            }

            if (menu instanceof AutoUpdateMenu autoUpdate) {
                if (autoUpdate.shouldReloadMenu()) {
                    menu.reload();
                    menu.render(inventory, player);
                    return period;
                }
            }

            buttons.forEach((slot, button) -> {
                if (button instanceof AutoUpdateButton autoUpdate) {
                    if (autoUpdate.shouldRerender(player)) {
                        ItemStack renderedItem = menu.render(slot, button, player);
                        inventory.setItemStack(slot, renderedItem);
                    }
                }
            });
        } catch (Throwable t) {
            player.sendMessage(MM."<red>There was an error auto-updating this menu!");
            player.closeInventory();
            return TaskSchedule.stop();
        }

        return period;
    }
}
