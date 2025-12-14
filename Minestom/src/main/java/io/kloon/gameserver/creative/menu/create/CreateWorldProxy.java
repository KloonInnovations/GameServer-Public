package io.kloon.gameserver.creative.menu.create;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.CreativeWorldsMenu;
import io.kloon.gameserver.creative.menu.commands.CreateWorldCommand;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.ranks.StoreRank;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class CreateWorldProxy implements ChestButton {
    private final CreativeWorldsMenu parent;

    public CreateWorldProxy(CreativeWorldsMenu parent) {
        this.parent = parent;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        new CreateWorldMenu(parent).display(player);
    }

    @Override
    public ItemStack renderButton(Player p) {
        KloonPlayer player = (KloonPlayer) p;
        Component name = MM."<title>Create New World";

        List<Component> lore = new ArrayList<>();

        List<WorldDef> existing = this.parent.getLiveWorldDefs();
        int limit = CreateWorldCommand.getWorldsLimit(player);
        lore.addAll(MM_WRAP."<gray>You're using <#FF266E>\{existing.size()}<#C87EB7>/\{limit} <gray>world slots that you may have on the server.");
        lore.add(Component.empty());
        if (existing.size() > limit) {
            lore.addAll(MM_WRAP."<gray>You have more worlds than you have world slots! Impressive! <light_purple>\uD83D\uDC4D"); // 👍
        } else if (existing.size() == limit - 1) {
            lore.addAll(MM_WRAP."<gray>Only one more slot left! <yellow>eek! \uD83E\uDD74"); // 🥴
        } else if (existing.size() < limit) {
            lore.addAll(MM_WRAP."<gray>Plenty of room to build new stuff!");
        } else {
            lore.add(MM."<red>Uh oh! No more slots!");
            if (player.getRanks().getLatestStoreRank() == StoreRank.NONE) {
                lore.add(MM."<gold>Visit https://kloon.io/store!");
            }
        }

        lore.add(Component.empty());
        if (existing.size() >= limit) {
            lore.add(MM."<!cta>Reached max number of worlds!");
        } else {
            lore.add(MM."<cta>Click to create a world!");
        }

        return MenuStack.of(Material.CRAFTING_TABLE).name(name).lore(lore).build();
    }
}
