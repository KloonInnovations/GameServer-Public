package io.kloon.gameserver.creative.menu.create.dimensions;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.menu.create.CreateWorldMenu;
import io.kloon.gameserver.creative.menu.create.WorldCreationState;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.world.DimensionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class ChooseDimensionMenu extends ChestMenu {
    private final CreateWorldMenu menu;

    public ChooseDimensionMenu(CreateWorldMenu menu) {
        super("Choose Dimension");
        this.menu = menu;
    }

    @Override
    protected void registerButtons() {
        ChestLayouts.INSIDE.distribute(VANILLA_DIMENSIONS, (slot, choice) -> {
            reg(slot, new DimensionChoiceButton(menu, choice));
        });

        reg().goBack(menu);
    }

    public static final DimensionChoice OVERWORLD = new DimensionChoice(DimensionType.OVERWORLD, "Overworld", Material.DIRT);
    private static final List<DimensionChoice> VANILLA_DIMENSIONS = Arrays.asList(OVERWORLD);

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>World Dimension";

        WorldCreationState state = menu.getState();

        Lore lore = new Lore();
        lore.wrap("<gray>What Minecraft dimension does the world sit in.");
        lore.addEmpty();
        lore.add(MM."<gray>Dimension: <green>\{state.getDimension().name()}");
        lore.addEmpty();
        lore.add(MM_WRAP."<yellow>⚠ Note! <gray>The dimension can't change after creation!");
        lore.addEmpty();
        lore.add(MM."<cta>Click to pick!");

        return MenuStack.of(state.getDimension().icon()).name(name).lore(lore).build();
    }
}
