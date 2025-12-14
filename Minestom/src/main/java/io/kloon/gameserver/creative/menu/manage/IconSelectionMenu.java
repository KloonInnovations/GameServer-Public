package io.kloon.gameserver.creative.menu.manage;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.listing.MenuList;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.CreativeWorldsMenuProxy;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.defs.WorldDefRepo;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class IconSelectionMenu extends ChestMenu {
    private static final Logger LOG = LoggerFactory.getLogger(IconSelectionMenu.class);
    
    private final ChestMenu parentOfCreativeWorlds;
    private final ChestMenu parent;
    private final WorldDef world;

    private final MenuList<Material> menuList = new MenuList<>(this, ChestLayouts.INSIDE, SelectIconButton::new)
            .withSearch(BlockFmt::getName);

    public IconSelectionMenu(ChestMenu parentOfCreativeWorlds, ChestMenu parent, WorldDef world) {
        super("World Icon Selection");
        this.parentOfCreativeWorlds = parentOfCreativeWorlds;
        this.parent = parent;
        this.world = world;

        setTitleFunction(getTitleFunction().andThen(menuList::titleWithPages));
    }

    @Override
    protected void registerButtons() {
        List<Material> materials = new ArrayList<>(Material.values());
        menuList.distribute(materials, this::reg);
        reg().goBack(parent);
    }

    public class SelectIconButton implements ChestButton {
        private final Material icon;

        public SelectIconButton(Material icon) {
            this.icon = icon;
        }

        @Override
        public void clickButton(Player p, ButtonClick click) {
            KloonPlayer player = (KloonPlayer) p;

            if (world.menuIcon().equals(icon)) {
                new CreativeWorldsMenuProxy(parentOfCreativeWorlds, Kgs.INSTANCE.getWorldListsCache()).fetchAndHandleClick(player);
                player.sendPit(NamedTextColor.RED, "NOPE!", MM."<gray>It already has this icon!");
                return;
            }

            world.setMenuIcon(icon);

            WorldDefRepo repo = Kgs.getCreativeRepos().defs();
            repo.update(world).whenCompleteAsync((_, t) -> {
                if (t != null) {
                    LOG.error("Error setting icon on world", t);
                    player.sendPitError(MM."<gray>There was an error editing the world info!");
                    return;
                }

                world.broadcastInvalidate();
                player.sendPit(NamedTextColor.DARK_GREEN, "NICE ICON!", MM."<gray>Changed the icon of <green>\{world.name()} <gray>to <green>\{BlockFmt.getName(icon)}<gray>!");
                new CreativeWorldsMenuProxy(parentOfCreativeWorlds, Kgs.INSTANCE.getWorldListsCache()).fetchAndHandleClick(player);
            }, player.scheduler());
        }

        @Override
        public ItemStack renderButton(Player player) {
            List<Component> lore = new ArrayList<>();
            lore.add(MM."<dark_gray>ID \{icon.key().value()}");
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to use icon!");

            return MenuStack.of(icon)
                    .name(MM."<title>\{BlockFmt.getName(icon)}")
                    .lore(MM_WRAP."<gray>")
                    .build();
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Edit Icon";

        List<Component> lore = MM_WRAP."<gray>Select a different icon for this world.";
        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<light_purple>This choice is cosmetic only!");
        lore.add(Component.empty());
        lore.add(MM."<yellow>Click to select icon!");

        return MenuStack.of(world.menuIcon()).name(name).lore(lore).build();
    }
}
