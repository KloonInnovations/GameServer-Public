package io.kloon.gameserver.chestmenus.listing;

import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.StaticButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayout;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleButton;
import io.kloon.gameserver.chestmenus.listing.filter.MenuFilter;
import io.kloon.gameserver.chestmenus.listing.filter.MenuFilterGroup;
import io.kloon.gameserver.chestmenus.listing.search.MenuSearch;
import io.kloon.gameserver.chestmenus.listing.search.MenuSearchButton;
import io.kloon.gameserver.chestmenus.listing.sort.MenuSort;
import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.chestmenus.pagination.MenuPagination;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class MenuList<T> {
    private final ChestMenu menu;
    private final MenuPagination pagination;
    private final BiFunction<Integer, T, ChestButton> buttonFunction;

    private final List<MenuFilterGroup<T>> filterGroups = new ArrayList<>();
    private final Cycle<MenuSort<T>> sorts = new Cycle<>();
    private MenuSearch<T> search = null;

    private Consumer<CycleButton<MenuSort<T>>> sortButtonModifier = button ->
            button.withTitle(MM."<title>Sort").withIcon(Material.HOPPER);

    public MenuList(ChestMenu menu, ChestLayout layout, Function<T, ChestButton> buttonFunction) {
        this(menu, layout, (_, thing) -> buttonFunction.apply(thing));
    }

    public MenuList(ChestMenu menu, ChestLayout layout, BiFunction<Integer, T, ChestButton> buttonFunction) {
        this.menu = menu;
        this.pagination = new MenuPagination(menu, layout);
        this.buttonFunction = buttonFunction;
    }

    public Component titleWithPages(Component title) {
        return pagination.titleWithPages(title);
    }

    public MenuList<T> withFilterGroup(MenuFilterGroup<T> filterGroup) {
        filterGroups.add(filterGroup);
        return this;
    }

    public MenuList<T> withSort(MenuSort<T> sort) {
        this.sorts.add(sort);
        return this;
    }

    public MenuList<T> withSort(String label, Comparator<T> comparator) {
        return withSort(new MenuSort<>(label, comparator));
    }

    public MenuList<T> withSortButton(Consumer<CycleButton<MenuSort<T>>> modifier) {
        this.sortButtonModifier = modifier;
        return this;
    }

    public MenuList<T> withSearch(Function<T, String> stringFunction) {
        return withSearch(new MenuSearch<>(stringFunction));
    }

    public MenuList<T> withSearch(MenuSearch<T> search) {
        this.search = search;
        return this;
    }

    public void distribute(List<T> everything, BiConsumer<Integer, ChestButton> slotsConsumer) {
        List<T> things = new ArrayList<>(everything);

        int filterSlot = menu.size().bottomCenter() + 1;
        if (search != null) {
            things = search.apply(things);
            slotsConsumer.accept(filterSlot, new MenuSearchButton<>(search));
            ++filterSlot;
        }

        for (int i = 0; i < filterGroups.size(); i++) {
            MenuFilterGroup<T> group = filterGroups.get(i);
            things.removeIf(group.getPredicate().negate());
            CycleButton<MenuFilter<T>> filterButton = new CycleButton<>(group.getCycle());
            group.modifyButton(filterButton);
            slotsConsumer.accept(filterSlot, filterButton);
            ++filterSlot;
        }

        if (things.isEmpty()) {
            slotsConsumer.accept(22, new StaticButton(MenuStack.of(Material.BARRIER)
                    .name(MM."<red>It's empty!")
                    .lore(MM_WRAP."<gray>There's nothing here!")));
            return;
        }

        MenuSort<T> menuSort = sorts.getSelected();
        if (menuSort != null) {
            things.sort(menuSort.sort());
            int sortSlot = menu.size().minus(5);
            CycleButton<MenuSort<T>> sortButton = new CycleButton<>(sorts);
            sortButtonModifier.accept(sortButton);
            slotsConsumer.accept(sortSlot, sortButton);
        }

        pagination.distribute(things, buttonFunction, slotsConsumer);
    }
}
