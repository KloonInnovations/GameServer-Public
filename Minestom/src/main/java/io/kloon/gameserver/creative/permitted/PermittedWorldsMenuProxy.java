package io.kloon.gameserver.creative.permitted;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.async.AsyncPlayerButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.CreativeWorldsMenu;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PermittedWorldsMenuProxy extends AsyncPlayerButton<List<WorldDef>> {
    private final CreativeWorldsMenu parent;

    public PermittedWorldsMenuProxy(CreativeWorldsMenu parent, int slot) {
        super(parent, slot);
        this.parent = parent;
    }

    @Override
    public CompletableFuture<List<WorldDef>> fetchData(Player p) {
        if (!(p instanceof KloonPlayer player)) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        ObjectId accountId = player.getAccountId();
        return Kgs.INSTANCE.getCreativeWorldsRepo().defs().getWorldsForPermitOwner(accountId);
    }

    @Override
    public void handleClickWithData(Player player, ButtonClick click, List<WorldDef> worlds) {
        if (worlds.isEmpty()) {
            return;
        }

        new PermittedWorldsMenu(parent, worlds).display(player);
    }

    @Override
    public ItemStack renderWithData(Player player, List<WorldDef> worlds) {
        if (worlds.isEmpty()) {
            Component name = MM."<title>Build & Play Together!";
            Lore lore = new Lore();
            lore.wrap("<gray>Use <#FF266E>/join <username> <gray>to join the instance of other players on the network.");
            return MenuStack.of(Material.LECTERN, name, lore);
        }

        Component name = MM."<gold>Worlds with Build Permits";

        Lore lore = new Lore();
        lore.wrap(worlds.size() == 1
                ? MM."<gray>You have a permit to build on <gold>\{worlds.size()} world <gray>owned by another player."
                : MM."<gray>You have a permit to build on <gold>\{worlds.size()} worlds <gray>owned by other players.");
        lore.addEmpty();
        lore.add("<cta>Click to view them!");

        return MenuStack.of(Material.LECTERN, name, lore);
    }

    @Override
    protected Material getLoadingIcon() {
        return Material.LECTERN;
    }
}
