package io.kloon.gameserver.chestmenus.pagination;

import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.layout.ChestLayout;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.minestom.server.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MenuPagination {
    private final ChestMenu menu;

    private final ChestLayout layout;

    private int pageIndex;
    private int lastSize = -1;

    public MenuPagination(ChestMenu menu, ChestLayout layout) {
        this.menu = menu;
        this.layout = layout;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) lastSize / layout.size());
    }

    public void setPageIndexAndRefresh(Player player, int pageIndex) {
        this.pageIndex = Math.max(0, pageIndex);
        ChestMenuInv.rerender(player);
    }

    public Component titleWithPages(Component title) {
        if (lastSize == 0) {
            return title;
        }

        int current = pageIndex + 1;
        int total = getTotalPages();
        if (total <= 1) {
            return title;
        }

        return title.append(MM." (\{current}/\{total})".style(Style.empty()));
    }

    public void distribute(List<? extends ChestButton> buttons, BiConsumer<Integer, ChestButton> slotsConsumer) {
        distribute(buttons, (slot, button) -> button, slotsConsumer);
    }

    public <T> void distribute(List<T> things, BiFunction<Integer, T, ChestButton> buttonFunction, BiConsumer<Integer, ChestButton> slotsConsumer) {
        boolean changedSize = lastSize != things.size();
        this.lastSize = things.size();
        if (changedSize) {
            this.pageIndex = 0;
        }

        ChestSize menuSize = menu.size();

        int pageSize = layout.size();
        int fromIndex = pageIndex * pageSize;
        int toIndex = (pageIndex + 1) * pageSize;
        List<T> onPage = things.subList(fromIndex, Math.min(things.size(), toIndex));
        layout.distribute(onPage, (slot, thing) -> {
            ChestButton button = buttonFunction.apply(slot, thing);
            slotsConsumer.accept(slot, button);
        });

        boolean hasPrevious = pageIndex > 0;
        if (hasPrevious) {
            slotsConsumer.accept(menuSize.minus(8), new PreviousPageButton(this));
        }

        boolean hasNext = toIndex < things.size();
        if (hasNext) {
            slotsConsumer.accept(menuSize.minus(0), new NextPageButton(this));
        }
    }
}
