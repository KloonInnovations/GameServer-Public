package io.kloon.gameserver.creative;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.async.AsyncFetchOnClickButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.storage.WorldListsCache;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.owner.player.PlayerWorldOwner;
import io.kloon.gameserver.modes.ModeType;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.RandUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class CreativeWorldsMenuProxy extends AsyncFetchOnClickButton<List<WorldDef>> {
    private final ChestMenu parent;
    private final WorldListsCache worldLists;

    public CreativeWorldsMenuProxy(ChestMenu parent, WorldListsCache worldLists) {
        this.parent = parent;
        this.worldLists = worldLists;
    }

    @Override
    public CompletableFuture<List<WorldDef>> fetchData(KloonPlayer player) {
        return worldLists.get(new PlayerWorldOwner(player));
    }

    @Override
    public void handleClickWithData(KloonPlayer player, List<WorldDef> worldDefs) {
        new CreativeWorldsMenu(parent, worldDefs).display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Your Creative Worlds";

        List<Component> lore = new ArrayList<>();
        if (Kgs.getModeType() == ModeType.CREATIVE) {
            lore.addAll(MM_WRAP."<gray>Browse your creative worlds or create a new one.");
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to view your worlds!");
        } else {
            lore.addAll(MM_WRAP."<gray>Turn your <rainbow>imagination</rainbow> into voxels on the creative server.");
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to view your worlds!");
        }

        Material icon = RandUtil.getRandom(GLAZED);
        return MenuStack.of(icon).name(name).lore(lore).build();
    }

    public static final List<Material> GLAZED = Arrays.asList(
            Material.WHITE_GLAZED_TERRACOTTA,
            Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
            Material.GRAY_GLAZED_TERRACOTTA,
            Material.BLACK_GLAZED_TERRACOTTA,
            Material.BROWN_GLAZED_TERRACOTTA,
            Material.RED_GLAZED_TERRACOTTA,
            Material.ORANGE_GLAZED_TERRACOTTA,
            Material.YELLOW_GLAZED_TERRACOTTA,
            Material.LIME_GLAZED_TERRACOTTA,
            Material.GREEN_GLAZED_TERRACOTTA,
            Material.CYAN_GLAZED_TERRACOTTA,
            Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Material.BLUE_GLAZED_TERRACOTTA,
            Material.PURPLE_GLAZED_TERRACOTTA,
            Material.MAGENTA_GLAZED_TERRACOTTA,
            Material.PINK_GLAZED_TERRACOTTA
    );
}
