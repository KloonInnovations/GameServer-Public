package io.kloon.gameserver.creative.menu.recycle;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.CreativeWorldsMenuProxy;
import io.kloon.gameserver.creative.menu.commands.CreateWorldCommand;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.defs.WorldDefRepo;
import io.kloon.gameserver.creative.storage.deletion.WorldDeletion;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.formatting.TimeFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class RecycledWorldButton implements ChestButton {
    private static final Logger LOG = LoggerFactory.getLogger(RecycledWorldButton.class);

    private final ChestMenu parentOfCreativeWorlds;
    private final WorldDef world;

    public RecycledWorldButton(ChestMenu parentOfCreativeWorlds, WorldDef world) {
        this.parentOfCreativeWorlds = parentOfCreativeWorlds;
        this.world = world;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        KloonPlayer player = (KloonPlayer) p;

        WorldDefRepo repo = Kgs.getCreativeRepos().defs();

        WorldOwner worldOwner = world.owner();
        int worldsLimit = CreateWorldCommand.getWorldsLimit(player);

        worldOwner.runWithLock(Kgs.getInfra(), player, () -> {
            return repo.countLiveWorldsByOwner(worldOwner).thenComposeAsync(count -> {
                if (count + 1 > worldsLimit) {
                    player.scheduleNextTick(e -> {
                        player.sendMessage(MM."<red>Cannot restore as it would exceed the maximum number of worlds!");
                        player.closeInventory();
                    });
                    return CompletableFuture.completedFuture(null);
                }

                world.setDeletion(null);
                return repo.update(world).whenCompleteAsync((_, t) -> {
                    if (t != null) {
                        LOG.error("Error removing deletion on world", t);
                        player.sendPitError(MM."<gray>There was an error restoring the world! Its data isn't lost, but there was a problem setting it as restored.");
                        return;
                    }

                    world.broadcastInvalidate();
                    player.sendPit(NamedTextColor.DARK_GREEN, "RESTORED!", MM."<gray>World <green>\{world.name()} <gray>was restored!");
                    new CreativeWorldsMenuProxy(parentOfCreativeWorlds, Kgs.INSTANCE.getWorldListsCache()).fetchAndHandleClick(player);
                }, player.scheduler());
            });
        });

        new CreativeWorldsMenuProxy(parentOfCreativeWorlds, Kgs.INSTANCE.getWorldListsCache()).fetchAndHandleClick(p);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>\{world.name()}";

        WorldDeletion deletion = world.deletion();
        if (deletion == null) {
            return MenuStack.of(Material.BARRIER).name(name).lore(MM_WRAP."<red>This world isn't deleted!").build();
        }

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(MM."<gray>Deleted: <red>\{TimeFmt.naturalTime(deletion.timestamp())}");
        lore.add(Component.empty());
        lore.add(MM."<yellow>Click to restore!");

        return MenuStack.of(Material.REDSTONE_BLOCK).name(name).lore(lore).build();
    }
}
