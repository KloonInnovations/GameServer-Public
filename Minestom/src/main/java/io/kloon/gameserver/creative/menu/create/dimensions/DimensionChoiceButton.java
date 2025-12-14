package io.kloon.gameserver.creative.menu.create.dimensions;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.menu.create.CreateWorldMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class DimensionChoiceButton implements ChestButton {
    private final CreateWorldMenu menu;
    private final DimensionChoice choice;

    public DimensionChoiceButton(CreateWorldMenu menu, DimensionChoice choice) {
        this.menu = menu;
        this.choice = choice;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        menu.getState().setDimension(choice);
        menu.display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>\{choice.name()}";

        List<Component> lore = new ArrayList<>();
        lore.addAll(MM_WRAP."<gray>It's a dimension.");
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to choose!");

        return MenuStack.of(choice.icon()).name(name).lore(lore).build();
    }
}
