package io.kloon.gameserver.creative.menu;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import humanize.Humanize;
import io.kloon.bigbackend.client.games.CreativeClient;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.MiniMessageTemplate;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.async.AsyncPlayerButton;
import io.kloon.gameserver.chestmenus.util.ChestButtonCooldown;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.CreativeWorldsMenu;
import io.kloon.gameserver.creative.menu.manage.ManageWorldMenu;
import io.kloon.gameserver.creative.storage.WorldListsCache;
import io.kloon.gameserver.creative.storage.defs.BuildPermit;
import io.kloon.gameserver.creative.storage.defs.WorldCopyInfo;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.creative.storage.saves.WorldSaveRepo;
import io.kloon.gameserver.modes.creative.buildpermits.menu.BuildPermitButton;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.formatting.TimeFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.creative.menu.CreativeWorldButton.*;

public class CreativeWorldButton extends AsyncPlayerButton<Data> {
    private final ChestMenu parent;
    private final WorldDef worldDef;

    private static final ChestButtonCooldown THROTTLE = new ChestButtonCooldown();
    private static final AsyncLoadingCache<ObjectId, WorldSave> LATEST_SAVE_BY_WORLD_ID = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .buildAsync((objectId, executor) -> {
                WorldSaveRepo savesRepo = Kgs.getCreativeRepos().saves();
                return savesRepo.getLatestSave(objectId);
            });


    public CreativeWorldButton(ChestMenu parent, int slot, WorldDef worldDef) {
        super(parent, slot);
        this.parent = parent;
        this.worldDef = worldDef;
    }

    private boolean canManage(KloonPlayer player) {
        return worldDef.ownership().isOwner(player);
    }

    @Override
    public CompletableFuture<Data> fetchData(Player player) {
        return LATEST_SAVE_BY_WORLD_ID.get(worldDef._id()).thenApply(save -> new Data(worldDef, save));
    }

    @Override
    public void handleClickWithData(Player p, ButtonClick click, Data data) {
        KloonPlayer player = (KloonPlayer) p;
        boolean isInWorld = CreativeWorldsMenu.isInWorld(player, worldDef);

        if (!canManage(player)) {
            if (!isInWorld) {
                warpToWorld(player);
            }
            return;
        }

        if (isInWorld) {
            openManageMenu(player);
            return;
        }

        if (click.isRightClick()) {
            openManageMenu(player);
        } else {
            warpToWorld(player);
        }
    }

    private void warpToWorld(KloonPlayer player) {
        if (!THROTTLE.check(player)) {
            return;
        }

        CreativeClient creative = Kgs.getBackend().getCreative();
        player.sendMessage(MM."<gray>Sending you to creative...");
        player.allocateAndTransfer(p -> creative.allocLatestSaveTransfer(p, worldDef._id(), worldDef.datacenter()));
    }

    private void openManageMenu(KloonPlayer player) {
        if (parent instanceof CreativeWorldsMenu worldsMenu) { // eek
            new ManageWorldMenu(worldsMenu, worldDef).display(player);
        } else {
            player.sendMessage(MM."<red>Uhm...");
        }
    }

    @Override
    public ItemStack renderWhileLoading(Player p) {
        KloonPlayer player = (KloonPlayer) p;

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(MM."<dark_gray>Loading latest save...");
        lore.add(Component.empty());

        renderCtaLore(player, lore);

        return MenuStack.of(worldDef.menuIcon())
                .name(MM."<title>\{worldDef.name()}")
                .lore(lore).build();
    }

    @Override
    public ItemStack renderWithData(Player p, Data data) {
        KloonPlayer player = (KloonPlayer) p;

        WorldSave save = data.latestSave;
        WorldOwner worldOwner = worldDef.owner();

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        if (save == null) {
            lore.addAll(MM_WRAP."<gray>This world doesn't have any saves. Hopefully that's not unexpected. <red>:S");
            lore.add(Component.empty());
        } else {
            lore.add(MM."<gray>Last edit: <aqua>\{TimeFmt.date(save.timestamp())}");
            lore.add(MM."<gray>Which was: <#FF266E>\{Humanize.naturalTime(new Date(save.timestamp()))}");
            lore.add(MM."<dark_gray>Server: \{save.serverCuteName()}");
            lore.add(Component.empty());

            lore.add(MM."<gray>Created: <#18D5FF>\{TimeFmt.date(worldDef.creationTimestamp(), "MMM dd yyyy")}");
            lore.add(MM."<gray>Region: <\{MiniMessageTemplate.INFRA_COLOR.asHexString()}>\{worldDef.datacenter().getRegionName()}");

            List<WorldCopyInfo> copyChain = worldDef.copyChain();
            if (!copyChain.isEmpty()) {
                lore.addAll(renderCopyChain(worldOwner, copyChain));
            }
            lore.add(Component.empty());
        }

        BuildPermit permit = worldDef.getPermitForPlayer(player.getAccountId());
        if (permit != null) {
            lore.add(MM."<gold>Build Permit");
            lore.addAll(BuildPermitButton.renderPermitDetails(permit));
            lore.add(Component.empty());
        }

        renderCtaLore(player, lore);

        return MenuStack.of(worldDef.menuIcon())
                .name(MM."<title>\{worldDef.name()}")
                .lore(lore).build();
    }

    private void renderCtaLore(KloonPlayer player, List<Component> lore) {
        boolean isInWorld = CreativeWorldsMenu.isInWorld(player, worldDef);
        if (!canManage(player)) {
            if (isInWorld) {
                lore.add(MM."<dark_green>\uD83D\uDCCC <green>You are here!");
            } else {
                lore.add(MM."<cta>Click to join world!");
            }
            return;
        }

        if (isInWorld) {
            lore.add(MM."<dark_green>\uD83D\uDCCC <green>You are here!");
            lore.add(MM."<cta>Click to manage!");
        } else {
            lore.add(MM."<rcta>Click to manage!");
            lore.add(MM."<lcta>Click to join world!");
        }
    }

    private static List<Component> renderCopyChain(WorldOwner newOwner, List<WorldCopyInfo> copyChain) {
        List<Component> lore = new ArrayList<>();
        StringBuilder indent = new StringBuilder();
        for (WorldCopyInfo copy : copyChain) {
            String copyNameMM = getCopiedWorldNameMM(newOwner, copy);
            lore.add(MM."<dark_gray>\{indent.toString()}⤷ <gray>Copied from \{copyNameMM}");
            indent.append(" ");
        }
        return lore;
    }

    private static String getCopiedWorldNameMM(WorldOwner newOwner, WorldCopyInfo copy) {
        WorldListsCache cache = Kgs.INSTANCE.getWorldListsCache();
        WorldDef knownWorld = cache.getWorldIfPresent(newOwner, copy.worldId());
        String nameOnCopy = copy.worldName();
        if (knownWorld == null || knownWorld.name().equals(nameOnCopy)) {
            return STR."<aqua>\{nameOnCopy}";
        }
        return STR."<aqua>\{nameOnCopy} (<green>now \{knownWorld.name()})";
    }

    public record Data(WorldDef worldDef, @Nullable WorldSave latestSave) {}
}
