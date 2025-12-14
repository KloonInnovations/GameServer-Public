package io.kloon.gameserver.modes.creative.buildpermits.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.builtin.StaticButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.storage.defs.BuildPermit;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitMenu;
import io.kloon.gameserver.modes.creative.commands.BuildPermitsCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class BuildPermitsMenu extends ChestMenu {
    public static final String ICON = "\uD83E\uDDBA";

    private final ChestMenu parent;
    private final CreativePlayer player;
    private final CreativeInstance instance;

    public static final int MAX_PERMITS = 10;

    public BuildPermitsMenu(ChestMenu parent, CreativePlayer player) {
        super(STR."\{ICON} Build Permits", ChestSize.FIVE);
        this.parent = parent;
        this.instance = player.getInstance();
        this.player = player;
    }

    public CreativePlayer getPlayer() {
        return player;
    }

    public CreativeInstance getInstance() {
        return instance;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        reload();
        super.clickButton(player, click);
    }

    public static boolean canManagePermits(CreativePlayer player) {
        CreativeInstance instance = player.getInstance();
        return instance.getWorldDef().ownership().isOwner(player);
    }

    @Override
    protected void registerButtons() {
        boolean canManage = canManagePermits(player);

        List<BuildPermit> permits = instance.getBuildPermits();
        if (permits.isEmpty()) {
            reg(size.middleCenter(), new StaticButton(MenuStack.of(Material.ARMS_UP_POTTERY_SHERD)
                    .name(MM."<yellow>No permits!")
                    .lore(MM_WRAP."<gray>No one else than you can build on this world and that's fine!")));
        } else {
            ChestLayouts.INSIDE.distribute(permits, (slot, permit) -> {
                reg(slot, new BuildPermitButton(this, slot, permit, canManage));
            });
        }

        reg().goBack(parent);
        if (canManage) {
            reg(size.bottomCenter() + 1, new CreatePermitMenu(this));
        }
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;
        Component name = MM."<gold>\{ICON} <title>Build Permits";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{BuildPermitsCommand.LABEL}");
        lore.addEmpty();

        List<BuildPermit> permits = instance.getBuildPermits();
        if (permits.isEmpty()) {
            lore.wrap("<gray>Allow other players to build on this world.");
        } else {
            lore.wrap(permits.size() == 1
                    ? MM."<gray>There is <gold>\{permits.size()} player <gray>with a permit to build on this world."
                    : MM."<gray>There are <gold>\{permits.size()} players <gray>with a permit to build on this world.");
        }
        lore.addEmpty();

        if (canManagePermits(player)) {
            lore.add("<cta>Click to manage permits!");
        } else {
            lore.add("<cta>Click to view permits!");
        }

        return MenuStack.of(Material.MINER_POTTERY_SHERD, name, lore);
    }
}
