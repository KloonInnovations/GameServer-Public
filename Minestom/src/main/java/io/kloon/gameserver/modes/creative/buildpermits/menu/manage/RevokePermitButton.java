package io.kloon.gameserver.modes.creative.buildpermits.menu.manage;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.storage.defs.BuildPermit;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.defs.WorldDefRepo;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.buildpermits.duration.EphemeralPermit;
import io.kloon.gameserver.modes.creative.buildpermits.duration.PermitDuration;
import io.kloon.gameserver.modes.creative.buildpermits.menu.BuildPermitsMenu;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTimeCooldownMap;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;
import io.kloon.infra.util.cooldown.impl.TimeCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class RevokePermitButton implements ChestButton {
    private final BuildPermitsMenu permitsMenu;
    private final BuildPermit permit;
    private final KloonMoniker permitOwner;

    private static final PlayerTimeCooldownMap COOLDOWN = new PlayerTimeCooldownMap(4, TimeUnit.SECONDS);

    public RevokePermitButton(BuildPermitsMenu permitsMenu, BuildPermit permit, KloonMoniker permitOwner) {
        this.permitsMenu = permitsMenu;
        this.permit = permit;
        this.permitOwner = permitOwner;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        CreativeInstance instance = player.getInstance();
        WorldDef worldDef = instance.getWorldDef();

        if (!COOLDOWN.get(p).cooldownIfPossible()) {
            player.playSound(SoundEvent.BLOCK_TRIPWIRE_CLICK_OFF, 0.5f);
            return;
        }

        PermitDuration duration = permit.duration();
        if (duration instanceof EphemeralPermit) {
            instance.getEphemeralPermits().remove(permit.minecraftUuid());
        } else {
            List<BuildPermit> persistedPermits = new ArrayList<>(worldDef.buildPermits());
            persistedPermits.removeIf(per -> per.isExpired(instance) || per.accountId().equals(permit.accountId()));
            worldDef.setPermits(persistedPermits);
        }

        worldDef.owner().runWithLock(Kgs.getInfra(), player, () -> {
            WorldDefRepo repo = Kgs.INSTANCE.getCreativeWorldsRepo().defs();
            return repo.update(worldDef).whenCompleteAsync((_, t) -> {
                if (t != null) {
                    player.sendPitError(MM."<gray>Couldn't revoke the build permit!");
                    player.closeInventory();
                    return;
                }

                KloonPlayer recipient = instance.getPlayerByUuid(permit.minecraftUuid());

                player.broadcast().sendExcept(recipient, MsgCat.WORLD, NamedTextColor.RED, "PERMIT REVOKED!", MM."<gray>Removed build permit from <white>\{permitOwner.getDisplayMM()}<gray>!",
                        SoundEvent.ITEM_GOAT_HORN_SOUND_4, 2.0);
                permitsMenu.reload().display(player);

                if (recipient != null) {
                    recipient.closeInventory();
                    recipient.playSound(SoundEvent.ITEM_GOAT_HORN_SOUND_4, 2.0);
                    recipient.sendPit(NamedTextColor.RED, "PERMIT REVOKED!", MM."<gray>Your build permit has been revoked!");
                }

                worldDef.broadcastInvalidate();
            });
        });
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<red>Revoke Build Permit";

        Lore lore = new Lore();
        lore.wrap("<gray>Removes the permission of building in this world.");
        lore.addEmpty();
        lore.add(MM."<gray>Player: \{permitOwner.getDisplayMM()}");
        lore.addEmpty();
        lore.add("<cta>Click to revoke!");

        return MenuStack.of(Material.TNT_MINECART, name, lore);
    }
}
