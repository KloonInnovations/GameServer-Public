package io.kloon.gameserver.creative.menu.create;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class EditNameButton implements ChestButton {
    private final CreateWorldMenu menu;

    public static final Material SIGN = Material.JUNGLE_SIGN;

    public EditNameButton(CreateWorldMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        KloonPlayer player = (KloonPlayer) p;

        String[] inputLines = SignUX.inputLines("Enter cool world", "name (-■_■)");
        SignUX.display(player, SIGN.block(), inputLines, input -> {
            menu.getState().setName(input[0]);
            menu.display(player);
        });
    }

    @Override
    public ItemStack renderButton(Player player) {
        String currentName = menu.getState().getName();

        Component name;
        List<Component> lore = new ArrayList<>();

        if (currentName == null) {
            name = MM."<title>Set World Name";

            lore.addAll(MM_WRAP."<gray>Pick a <#FF266E>cool name</#FF266E> for this new world.");
            lore.add(Component.empty());
            lore.add(MM."<gray><i>Don't worry</i>, you can edit later!");
            lore.addAll(MM_WRAP."<dark_gray>Doesn't have to be unique!");
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to pick name!");
        } else {
            name = MM."<title>Edit World Name";

            lore = new ArrayList<>();
            lore.add(MM."<gray>Name: <title>\{currentName}");

            lore.add(Component.empty());
            lore.addAll(MM_WRAP."<dark_gray>You can change the world's name after it's created!");
            lore.add(Component.empty());

            lore.add(MM."<cta>Click to edit!");
        }

        return MenuStack.of(SIGN).name(name).lore(lore).build();
    }
}
