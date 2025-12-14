package io.kloon.gameserver.creative.menu.manage.oldsaves;

import humanize.Humanize;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.async.AsyncPlayerButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.creative.storage.saves.WorldSaveRepo;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.ranks.StoreRank;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class WorldSavesMenuProxy extends AsyncPlayerButton<List<WorldSave>> {
    private final ChestMenu parent;
    private final WorldDef world;

    public WorldSavesMenuProxy(ChestMenu parent, int slot, WorldDef world) {
        super(parent, slot);
        this.parent = parent;
        this.world = world;
    }

    @Override
    public CompletableFuture<List<WorldSave>> fetchData(Player player) {
        WorldSaveRepo savesRepo = Kgs.getCreativeRepos().saves();
        return savesRepo.getSaves(world);
    }

    @Override
    public void handleClickWithData(Player player, ButtonClick click, List<WorldSave> worldSaves) {
        if (worldSaves.isEmpty()) {
            return;
        }

        new WorldSavesMenu(parent, world, worldSaves).display(player);
    }

    @Override
    public ItemStack renderWithData(Player p, List<WorldSave> worldSaves) {
        KloonPlayer player = (KloonPlayer) p;

        Component name = MM."<title>World Saves";

        List<Component> lore = new ArrayList<>();
        if (worldSaves.isEmpty()) {
            lore.addAll(MM_WRAP."<red>There are no saves yet on this world!");
        } else if (worldSaves.size() == 1) {
            lore.addAll(MM_WRAP."<gray>This world only has <gold>\{worldSaves.size()} <gray>save.");
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to view it!");
        } else {
            lore.addAll(MM_WRAP."<gray>This world has <gold>\{worldSaves.size()} <gray>saves which you can load in case something went wrong.");
            lore.add(Component.empty());

            StoreRank storeRank = player.getRanks().getLatestStoreRank();
            int retainedSaves = storeRank.getRetainedSaves();
            lore.addAll(MM_WRAP."<dark_gray>Saves past the \{Humanize.ordinal(retainedSaves)} are deleted as new ones are created.");
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to view saves!");
        }

        return MenuStack.of(getLoadingIcon()).name(name).lore(lore).build();
    }

    @Override
    protected Material getLoadingIcon() {
        return Material.CLOCK;
    }
}
