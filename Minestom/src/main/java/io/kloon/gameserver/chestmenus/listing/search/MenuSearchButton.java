package io.kloon.gameserver.chestmenus.listing.search;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.ChestMenuPlayer;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class MenuSearchButton<T> implements ChestButton {
    private final MenuSearch<T> menuSearch;

    public MenuSearchButton(MenuSearch<T> menuSearch) {
        this.menuSearch = menuSearch;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        if (!(player instanceof ChestMenuPlayer chestMenuPlayer)) {
            player.sendMessage(MM."<red>Can't display sign UX for this type of player!");
            player.closeInventory();
            return;
        }

        if (click.isSneakClick()) {
            menuSearch.fuzzy(!menuSearch.isFuzzy());
            ChestMenuInv.rerender(player);
            return;
        }

        if (click.isRightClick()) {
            menuSearch.setQuery(null);
            ChestMenuInv.rerender(player);
            return;
        }

        String query = menuSearch.getQuery();
        String[] inputLines = new String[] {
                query == null ? "" : query,
                SignUX.UNDERLINE,
                "Enter search",
                "query"
        };
        SignUX.display(chestMenuPlayer, inputLines, inputArray -> {
            String input = inputArray[0];
            menuSearch.setQuery(input);
            ChestMenuInv.rerender(player);
        });
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Search";

        boolean fuzzy = menuSearch.isFuzzy();

        List<Component> lore;
        String query = menuSearch.getQuery();
        if (query == null) {
            lore = MM_WRAP."<gray>Filter based on a search query.";
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to search!");
        } else {
            lore = MM_WRAP."<gray>Filter based on a search query. Use strict search if fuzzy is annoying you.";
            lore.add(Component.empty());
            lore.add(MM."<gray>Your query: <aqua>\"\{query }\"");
            lore.add(MM."<gray>Search mode: <dark_gray>\{fuzzy ? "Fuzzy" : "Strict"}");
            lore.add(Component.empty());
            Component fuzzyInstruction = fuzzy
                    ? MM."<darK_gray>\{InputFmt.SNEAK_CLICK} Click to toggle fuzzy!"
                    : MM."<darK_gray>\{InputFmt.SNEAK_CLICK} Click to toggle strict!";
            lore.add(fuzzyInstruction);
            lore.add(MM."<rcta>Click to reset!");
            lore.add(MM."<lcta>Click to edit search!");
        }
        return MenuStack.of(Material.OAK_HANGING_SIGN).name(name).lore(lore).build();
    }
}
