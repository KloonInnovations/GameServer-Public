package io.kloon.gameserver.modes.creative.menu.worldadmin.size;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldSize;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WorldSizeMenu extends ChestMenu {
    public static final String ICON = "\uD83D\uDCCF"; // 📏

    private final ChestMenu parent;

    public WorldSizeMenu(ChestMenu parent) {
        super("World Size", ChestSize.FOUR);
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        int[] layout = { 11, 12, 14, 15 };
        CreativeWorldSize[] sizes = CreativeWorldSize.values();

        for (int i = 0; i < Math.min(layout.length, sizes.length); ++i) {
            reg(layout[i], new WorldSizeButton(parent, sizes[i]));
        }

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<#00FF8C>\{ICON} <title>World Size";

        Lore lore = new Lore();
        lore.wrap(MM."<gray>Pick this world's size, which means the bounds within which you're allowed to build.");
        lore.addEmpty();

        CreativeWorldStorage storage = player.getInstance().getWorldStorage();
        CreativeWorldSize worldSize = storage.getWorldSize();
        lore.add(MM."<gray>Size: <green>\{worldSize.getCuteName()}");

        int diameter = worldSize.getChunksDiameter();
        lore.add(MM."<gray>Chunks: <light_purple>\{diameter}x\{diameter}");
        lore.addEmpty();

        lore.add(MM."<cta>Click to resize!");

        return MenuStack.of(worldSize.getIcon(), name, lore);
    }
}
