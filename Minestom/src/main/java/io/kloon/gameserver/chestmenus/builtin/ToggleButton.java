package io.kloon.gameserver.chestmenus.builtin;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class ToggleButton implements ChestButton {
    public abstract boolean isEnabled(Player player);

    public abstract void setEnabled(Player player, boolean enabled);

    public abstract void onValueChange(Player player, boolean newValue);

    @Override
    public final void clickButton(Player player, ButtonClick click) {
        toggle(player);
    }

    public final void toggle(Player player) {
        if (!canSeeButton(player)) {
            return;
        }

        boolean oldValue = isEnabled(player);
        boolean newValue = !oldValue;

        setEnabled(player, newValue);
        onValueChange(player, newValue);
    }

    public ItemBuilder2 getIcon(boolean enabled) {
        Material material = getIconMaterial(enabled);
        return MenuStack.of(material);
    }

    public abstract Material getIconMaterial(boolean enabled);

    public abstract String getName();

    public String getTextIcon() {
        return "";
    }

    public abstract List<Component> getDescription();

    public boolean canSeeButton(Player player) {
        return true;
    }

    @Override
    public ItemStack renderButton(Player player) {
        if (!canSeeButton(player)) {
            return ItemStack.AIR;
        }

        String icon = getTextIcon();
        Component title = (icon == null || icon.isEmpty())
                ? MM."<title>\{getName()}"
                : MM."\{icon} <title>\{getName()}";

        boolean enabled = isEnabled(player);

        List<Component> lore = new ArrayList<>(getDescription());
        lore.add(Component.empty());
        lore.add(getStateLine(enabled));

        lore.add(Component.empty());
        lore.add(MM."<cta>Click to toggle!");

        ItemBuilder2 builder = getIcon(enabled).name(title).lore(lore);
        if (enabled) {
            builder.glowing();
        }
        return builder.build();
    }

    protected Component getStateLine(boolean enabled) {
        return enabled ? MM."<gray>State: <green>ON!" : MM."<gray>State: <red>OFF!";
    }
}
