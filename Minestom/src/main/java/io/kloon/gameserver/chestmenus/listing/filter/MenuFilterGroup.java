package io.kloon.gameserver.chestmenus.listing.filter;

import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleButton;
import net.minestom.server.item.Material;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MenuFilterGroup<T> {
    private final Cycle<MenuFilter<T>> filters = new Cycle<>();

    private Consumer<CycleButton<MenuFilter<T>>> buttonModifier = button ->
            button.withTitle(MM."<title>Filter").withIcon(Material.PUMPKIN_PIE);

    public MenuFilterGroup<T> addAny(String label) {
        filters.add(new MenuFilter<>(label, obj -> true));
        return this;
    }

    public MenuFilterGroup<T> add(MenuFilter<T> filter) {
        filters.add(filter);
        return this;
    }

    public MenuFilterGroup<T> add(String label, Predicate<T> predicate) {
        return add(new MenuFilter<>(label, predicate));
    }

    public MenuFilterGroup<T> withButton(Consumer<CycleButton<MenuFilter<T>>> modifier) {
        this.buttonModifier = modifier;
        return this;
    }

    public void modifyButton(CycleButton<MenuFilter<T>> button) {
        buttonModifier.accept(button);
    }

    public Predicate<T> getPredicate() {
        if (filters.size() == 0) {
            return o -> true;
        }
        MenuFilter<T> filter = filters.getSelected();
        return filter.predicate();
    }

    public Cycle<MenuFilter<T>> getCycle() {
        return filters;
    }
}
