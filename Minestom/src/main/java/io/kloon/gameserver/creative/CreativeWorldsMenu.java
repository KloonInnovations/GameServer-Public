package io.kloon.gameserver.creative;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.GoBackButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.listing.MenuList;
import io.kloon.gameserver.creative.menu.NoWorldsFoundButton;
import io.kloon.gameserver.creative.menu.create.CreateWorldMenu;
import io.kloon.gameserver.creative.menu.CreativeWorldButton;
import io.kloon.gameserver.creative.menu.create.CreateWorldProxy;
import io.kloon.gameserver.creative.menu.recycle.RecycleBinMenu;
import io.kloon.gameserver.creative.permitted.PermittedWorldsMenuProxy;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import net.minestom.server.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class CreativeWorldsMenu extends ChestMenu {
    private final ChestMenu parent;
    private final List<WorldDef> worldDefs;

    public static Function<Player, WorldDef> GET_PLAYER_WORLD = p -> null;

    private final MenuList<WorldDef> menuList;

    public CreativeWorldsMenu(ChestMenu parent, List<WorldDef> worldDefs) {
        super("Your Creative Worlds");
        this.parent = parent;
        this.worldDefs = worldDefs;
        this.menuList = new MenuList<>(this, ChestLayouts.INSIDE, (slot, worldDef) -> new CreativeWorldButton(this, slot, worldDef));
    }

    public ChestMenu getParent() {
        return parent;
    }

    public List<WorldDef> getAllWorldDefs() {
        return worldDefs;
    }

    public List<WorldDef> getLiveWorldDefs() {
        return worldDefs.stream().filter(w -> !w.deleted())
                .sorted(Comparator.comparingInt(w -> w._id().getTimestamp()))
                .toList();
    }

    public List<WorldDef> getDeletedWorldDefs() {
        return worldDefs.stream().filter(WorldDef::deleted)
                .sorted(Comparator.comparingInt(w -> w._id().getTimestamp()))
                .toList();
    }

    public static boolean isInWorld(Player player, WorldDef def) {
        WorldDef playerWorld = GET_PLAYER_WORLD.apply(player);
        return playerWorld != null && playerWorld._id().equals(def._id());
    }

    @Override
    protected void registerButtons() {
        if (worldDefs.isEmpty()) {
            reg(22, new NoWorldsFoundButton());
            reg().goBack(parent);
            reg(size.bottomCenter(), new GoBackButton(parent));
            return;
        }

        List<WorldDef> liveWorlds = getLiveWorldDefs();
        List<WorldDef> deletedWorlds = getDeletedWorldDefs();

        menuList.distribute(liveWorlds, this::reg);

        reg(size.bottomCenter() - 3, slot -> new PermittedWorldsMenuProxy(this, slot));

        reg(size.bottomCenter() - 1, new RecycleBinMenu(this, deletedWorlds));
        reg().goBack(parent);
        reg(size.bottomCenter() + 1, new CreateWorldProxy(this));
    }
}