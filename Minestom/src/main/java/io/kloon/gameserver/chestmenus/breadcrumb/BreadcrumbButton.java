package io.kloon.gameserver.chestmenus.breadcrumb;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class BreadcrumbButton implements ChestButton {
    private final Material icon;
    private final List<MenuBreadcrumb> breadcrumbs;

    public BreadcrumbButton(Material icon) {
        this.icon = icon;
        this.breadcrumbs = new ArrayList<>();
    }

    private BreadcrumbButton(Material icon, List<MenuBreadcrumb> breadcrumbs) {
        this.icon = icon;
        this.breadcrumbs = breadcrumbs;
    }

    public BreadcrumbButton with(String title, String actionMM) {
        List<MenuBreadcrumb> copy = new ArrayList<>(breadcrumbs);
        copy.add(new MenuBreadcrumb(title, actionMM));
        return new BreadcrumbButton(icon, copy);
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player player) {
        if (breadcrumbs.isEmpty()) {
            return ItemStack.AIR;
        }

        MenuBreadcrumb last = breadcrumbs.getLast();
        Component name = MM."<title>\{last.inventoryTitle()}";

        Lore lore = new Lore();

        StringBuilder indentSb = new StringBuilder();
        for (int i = 0; i < breadcrumbs.size() - 1; ++i) {
            MenuBreadcrumb breadcrumb = breadcrumbs.get(i);
            String indent = indentSb.toString();
            lore.add(MM."<dark_gray>\{indent}⤷ <gray>\{breadcrumb.inventoryTitle()}");
            lore.add(MM."\{indent}  <dark_gray>\{breadcrumb.actionMM()}");
            indentSb.append(" ");
        }

        lore.add(MM."<dark_gray>\{indentSb.toString()}⤷ <title>You are here!");
        lore.add(MM."\{indentSb.toString()}  <dark_gray>\{last.actionMM()}");

        return MenuStack.of(icon, name, lore);
    }

    public Component generateTitle(Player player) {
        if (breadcrumbs.isEmpty()) {
            return MM."???";
        } else if (breadcrumbs.size() == 1) {
            return MM."\{breadcrumbs.getFirst().inventoryTitle()}";
        }

        MenuBreadcrumb first = breadcrumbs.getFirst();
        MenuBreadcrumb last = breadcrumbs.getLast();

        return MM."\{first.inventoryTitle()} ➜ \{last.inventoryTitle()}";
    }
}
