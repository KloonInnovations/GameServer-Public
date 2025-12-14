package io.kloon.gameserver.modes.creative.buildpermits.menu.create.duration;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.buildpermits.duration.PermitDuration;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PermitDurationButton implements ChestButton {
    private final CreatePermitMenu menu;

    private final Material icon;
    private final PermitDuration duration;

    public PermitDurationButton(CreatePermitMenu menu, Material icon, PermitDuration duration) {
        this.menu = menu;
        this.icon = icon;
        this.duration = duration;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        menu.getState().setDuration(duration);
        menu.reload().display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        String durationFmt = duration.formattedMM();
        Component name = MM."<title>\{durationFmt}";

        Lore lore = new Lore();
        lore.add(duration.lore());
        lore.addEmpty();
        lore.add("<cta>Click to select!");

        return MenuStack.of(icon, name, lore);
    }
}
