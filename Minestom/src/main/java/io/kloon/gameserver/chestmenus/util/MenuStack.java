package io.kloon.gameserver.chestmenus.util;

import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public final class MenuStack {
    private MenuStack() {}

    public static ItemBuilder2 of(Material material) {
        return new ItemBuilder2(material)
                .hideFlags();
    }

    public static ItemBuilder2 ofHead(HeadProfile profile) {
        return of(Material.PLAYER_HEAD)
                .set(DataComponents.PROFILE, profile);
    }

    public static ItemBuilder2 ofHead(String skinValue) {
        HeadProfile head = SkinCache.toHead(skinValue);
        return ofHead(head);
    }

    public static ItemStack of(Material material, Component name, Lore lore) {
        return of(material).name(name).lore(lore).build();
    }

    public static ItemBuilder2 extraLore(ItemStack item, Lore extraLore) {
        Component name = item.get(DataComponents.CUSTOM_NAME);
        if (name == null) {
            name = MM."<white>\{BlockFmt.getName(item.material())}";
        }

        Lore lore = new Lore();

        List<Component> stackLore = item.get(DataComponents.LORE);
        if (stackLore != null) {
            stackLore.forEach(lore::add);
        }

        lore.add(MM."<dark_gray><st>---------------");
        lore.addEmpty();
        lore.add(extraLore);

        return MenuStack.of(item.material()).name(name).lore(lore);
    }
}
