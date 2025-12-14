package io.kloon.gameserver.modes.creative.buildpermits.menu;

import humanize.Humanize;
import humanize.util.Constants;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.async.AsyncPlayerButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.storage.defs.BuildPermit;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.modes.creative.buildpermits.duration.EphemeralPermit;
import io.kloon.gameserver.modes.creative.buildpermits.duration.InfinitePermit;
import io.kloon.gameserver.modes.creative.buildpermits.duration.PermitDuration;
import io.kloon.gameserver.modes.creative.buildpermits.duration.TimedPermit;
import io.kloon.gameserver.modes.creative.buildpermits.menu.manage.ManagePermitMenu;
import io.kloon.gameserver.util.formatting.TimeFmt;
import io.kloon.infra.cache.KloonCaches;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class BuildPermitButton extends AsyncPlayerButton<BuildPermitButton.Data> {
    private final BuildPermitsMenu parent;
    private final BuildPermit permit;
    private final boolean canManage;

    public BuildPermitButton(BuildPermitsMenu parent, int slot, BuildPermit permit, boolean canManage) {
        super(parent, slot);
        this.parent = parent;
        this.permit = permit;
        this.canManage = canManage;
    }

    @Override
    public CompletableFuture<Data> fetchData(Player player) {
        KloonCaches caches = Kgs.getInfra().caches();
        CompletableFuture<KloonMoniker> getMoniker = caches.monikers().getByAccountId(permit.accountId());
        CompletableFuture<HeadProfile> getHead = getMoniker.thenCompose(SkinCache::get);
        return CompletableFuture.allOf(getMoniker, getHead).thenApply(_ -> new Data(getMoniker.join(), getHead.join()));
    }

    @Override
    public void handleClickWithData(Player player, ButtonClick click, Data data) {
        if (!canManage) return;

        new ManagePermitMenu(parent, permit, data.moniker).display(player);
    }

    @Override
    public ItemStack renderWithData(Player player, Data data) {
        KloonMoniker target = data.moniker;

        Component name = MM."\{target.getDisplayMM()}";

        Lore lore = new Lore();
        lore.add("<dark_gray>Build Permit");
        lore.addEmpty();

        lore.add(renderPermitDetails(permit));

        if (target.minecraftUuid().equals(player.getUuid())) {
            lore.addEmpty();
            lore.add("<gold>✨ <#FF266E>That's you!!");
        }

        if (canManage) {
            lore.addEmpty();
            lore.add("<cta>Click to manage!");
        }

        return MenuStack.of(Material.PLAYER_HEAD)
                .set(DataComponents.PROFILE, data.head)
                .name(name)
                .lore(lore)
                .build();
    }

    public static List<Component> renderPermitDetails(BuildPermit permit) {
        List<Component> lore = new ArrayList<>();
        String issuedFmt = TimeFmt.date(permit.issueTimestamp());
        lore.add(MM."<gray>Issued: <aqua>\{issuedFmt}");

        switch (permit.duration()) {
            case EphemeralPermit ephemeral -> {
                lore.add(MM."<gray>Duration: \{ephemeral.formattedMM()}");
                lore.add(MM."<dark_gray>Ends when you disconnect!");
            }
            case InfinitePermit infinite -> lore.add(MM."<gray>Duration: \{infinite.formattedMM()}");
            case TimedPermit timed -> {
                long remainingMs = timed.expiryMs(permit.issueTimestamp()) - System.currentTimeMillis();
                String durationFmt = Humanize.duration(remainingMs / 1000, Constants.TimeStyle.FRENCH_DECIMAL);
                lore.add(MM."<gray>Remaining: <green>\{durationFmt}");
            }
        }
        return lore;
    }

    public record Data(
            KloonMoniker moniker,
            HeadProfile head
    ) {}
}
