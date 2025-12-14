package io.kloon.gameserver.creative.menu.recycle;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.CreativeWorldsMenu;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class RecycleBinMenu extends ChestMenu {
    private final CreativeWorldsMenu parent;
    private final List<WorldDef> deletedWorlds;

    public RecycleBinMenu(CreativeWorldsMenu parent, List<WorldDef> deletedWorlds) {
        super("Worlds Recycle Bin");
        this.parent = parent;
        this.deletedWorlds = deletedWorlds;
    }

    @Override
    protected void registerButtons() {
        ChestLayouts.INSIDE.distribute(deletedWorlds, (slot, world) -> {
            reg(slot, new RecycledWorldButton(parent.getParent(), world));
        });

        reg().goBack(parent);
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        if (deletedWorlds.isEmpty()) {
            return;
        }

        super.clickButton(player, click);
    }

    @Override
    public ItemStack renderButton(Player player) {
        if (deletedWorlds.isEmpty()) {
            return ItemStack.AIR;
        }

        Component name = MM."<title>Recycle Bin";

        List<Component> lore;
        if (deletedWorlds.size() == 1) {
            lore = MM_WRAP."<gray>You have <red>1 <gray>recently deleted world.";
        } else {
            lore = MM_WRAP."<gray>You have <red>\{deletedWorlds.size()} <gray>recently deleted worlds.";
        }
        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<gray>Worlds in here <i>usually</i> remain for <gold>2 weeks<gray>, after which they are permanently removed.");
        lore.add(MM."<dark_gray><i>Worlds may be deleted sooner");
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to open bin!");

        return MenuStack.of(Material.CAULDRON).name(name).lore(lore).build();
    }
}
