package io.kloon.gameserver.creative.menu.manage.oldsaves;

import humanize.Humanize;
import humanize.util.Constants;
import io.kloon.bigbackend.client.games.CreativeClient;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.menu.manage.oldsaves.bruh.ManageOldSaveMenu;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.WordUtilsK;
import io.kloon.gameserver.util.formatting.TimeFmt;
import io.kloon.infra.util.cutenames.PetNames;
import io.kloon.infra.util.throttle.maps.ThrottleCooldownMap;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class OldSaveButton implements ChestButton {
    private final ChestMenu parent;
    private final OldWorldSave oldSave;
    private final WorldDef world;
    private final WorldSave save;

    private boolean canManage = true;
    private static final ThrottleCooldownMap<UUID> rightClickThrottle = new ThrottleCooldownMap<>(3, 12_000, 2_000);

    public OldSaveButton(ChestMenu parent, OldWorldSave oldSave) {
        this.parent = parent;
        this.oldSave = oldSave;
        this.world = oldSave.world();
        this.save = oldSave.save();
    }

    public OldSaveButton withCanManage(boolean canLoad) {
        this.canManage = canLoad;
        return this;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        if (!(p instanceof KloonPlayer player)) return;

        if (canManage && click.isRightClick()) {
            handleRightClick(player);
        } else {
            handleLeftClick(player);
        }
    }

    private void handleLeftClick(KloonPlayer player) {
        CreativeClient creative = Kgs.getCreative();
        player.sendMessage(MM."<gray>Sending you to the save...");
        player.allocateAndTransfer(tp -> creative.allocTransfer(tp, world._id(), save._id(), world.datacenter()));
    }

    private void handleRightClick(KloonPlayer player) {
        if (!rightClickThrottle.get(player.getUuid()).procIfPossible()) {
            player.sendMessage(MM."<red>Sorry! Loading saves for management is on cooldown!");
            return;
        }

        new ManageOldSaveMenu(parent, oldSave).display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name;
        List<Component> lore = new ArrayList<>();

        if (oldSave.isLatest()) {
            name = MM."<gold>\{save.cuteName()}";
            lore.add(MM."<dark_gray>Latest save");
        } else {
            name = MM."<green>\{save.cuteName()}";
            lore.add(MM."<dark_gray>Older save");
        }

        WorldSave prev = oldSave.prev();

        lore.add(Component.empty());
        lore.add(MM."<gray>Date: <aqua>\{TimeFmt.date(save.timestamp())}");
        lore.add(MM."<gray>Which was: <#FF266E>\{TimeFmt.naturalTime(save.timestamp())}");
        if (prev != null) {
            long elapsedSeconds = (save.timestamp() - prev.timestamp()) / 1000;
            lore.add(MM."<dark_gray>Since preceding: \{Humanize.duration(elapsedSeconds, Constants.TimeStyle.FRENCH_DECIMAL)}");
        }
        lore.add(Component.empty());

        lore.add(MM."<gray>Server: <blue>\{save.serverCuteName()}");
        lore.add(MM."<gray>Save reason: <#FF266E>\{WordUtilsK.enumName(save.reason())}");
        lore.add(MM."<dark_gray>\{Humanize.ordinal(save.indexInSession() + 1)} save on instance!");

        if (player.getInstance().getUniqueId().equals(save.instanceId())) {
            lore.add(Component.empty());
            lore.add(MM."<gold><bold>SAVED FROM THIS INSTANCE!");
        }

        lore.add(Component.empty());

        if (save.loadedFrom() == null) {
            lore.add(MM."<light_purple>Created from scratch!");
            lore.add(Component.empty());
        } else if (save.indexInSession() == 0) {
            String cuteName = PetNames.generate(save.loadedFrom());
            lore.add(MM."<gray>Loaded from: <green>\{cuteName}");
            if (prev != null && prev._id().equals(save.loadedFrom())) {
                lore.add(MM."<dark_gray>That's the preceding save!");
            } else {
                lore.add(MM."<dark_gray>That's an older save!");
            }
            lore.add(Component.empty());
        }

        if (canManage) {
            lore.add(MM."<rcta>Click to manage!");
            lore.add(MM."<lcta>Click to load!");
        } else {
            lore.add(MM."<cta>Click to load save!");
        }

        return MenuStack.of(world.menuIcon()).name(name).lore(lore).build();
    }
}