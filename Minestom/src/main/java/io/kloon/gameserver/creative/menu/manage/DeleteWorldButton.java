package io.kloon.gameserver.creative.menu.manage;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.CreativeWorldsMenu;
import io.kloon.gameserver.creative.CreativeWorldsMenuProxy;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.defs.WorldDefRepo;
import io.kloon.gameserver.creative.storage.deletion.WorldDeletion;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.redis.RedisRateLimit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class DeleteWorldButton implements ChestButton {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteWorldButton.class);

    private final ChestMenu parentOfCreativeWorlds;
    private final WorldDef world;

    public DeleteWorldButton(ChestMenu parentOfCreativeWorlds, WorldDef world) {
        this.parentOfCreativeWorlds = parentOfCreativeWorlds;
        this.world = world;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        KloonPlayer player = (KloonPlayer) p;

        WorldDeletion deletion = new WorldDeletion(player.getAccountId(), System.currentTimeMillis());
        WorldDefRepo repo = Kgs.getCreativeRepos().defs();

        WorldOwner worldOwner = world.owner();

        RedisRateLimit rateLimit = worldOwner.getDeletionRateLimit(Kgs.getInfra().redis());

        worldOwner.runWithLock(Kgs.getInfra(), player, () -> {
            return rateLimit.canProceed().thenComposeAsync(withinRateLimit -> {
                if (!withinRateLimit) {
                    player.scheduleNextTick(_ -> {
                        player.sendMessage(MM."<red>You may only delete so many worlds per day! Sorry!");
                        player.closeInventory();
                    });
                    return CompletableFuture.completedFuture(null);
                }

                world.setDeletion(deletion);

                return repo.update(world).whenCompleteAsync((_, t) -> {
                    if (t != null) {
                        LOG.error("Error setting deletion on world", t);
                        player.sendPitError(MM."<gray>There was an error deleting the world!");
                        player.closeInventory();
                        return;
                    }

                    player.sendPit(NamedTextColor.RED, "DELETED!", MM."<gray>World <green>\{world.name()} <gray>moved to recycle bin!");
                    world.broadcastInvalidate();
                    if (CreativeWorldsMenu.isInWorld(player, world)) {
                        player.closeInventory();
                    } else {
                        new CreativeWorldsMenuProxy(parentOfCreativeWorlds, Kgs.INSTANCE.getWorldListsCache()).fetchAndHandleClick(player);
                    }
                }, player.scheduler());
            });
        });
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<red>Delete World";

        List<Component> lore = MM_WRAP."<gray>Sends this world to the recycle bin to be deleted soon.";
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to delete!");

        return MenuStack.of(Material.TNT_MINECART).name(name).lore(lore).build();
    }
}
