package io.kloon.gameserver.modes.creative.buildpermits.menu.create;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.states.ButtonState;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class IssuePermitButton implements ChestButton {
    private final BuildPermitsMenu permitsMenu;
    private final CreatePermitMenu createMenu;

    private final CreatePermitState createState;

    private static final PlayerTimeCooldownMap COOLDOWN = new PlayerTimeCooldownMap(4, TimeUnit.SECONDS);

    public IssuePermitButton(BuildPermitsMenu permitsMenu, CreatePermitMenu createMenu) {
        this.permitsMenu = permitsMenu;
        this.createMenu = createMenu;
        this.createState = createMenu.getState();
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer issuer = (CreativePlayer) p;
        CreativeInstance instance = issuer.getInstance();

        ButtonState state = getState(issuer);
        if (state != CAN_CREATE) {
            state.sendChatMessage(issuer);
            return;
        }

        if (!COOLDOWN.get(p).cooldownIfPossible()) {
            issuer.playSound(SoundEvent.BLOCK_TRIPWIRE_CLICK_OFF, 0.5f);
            return;
        }

        KloonPlayer recipient = createState.getRecipient(instance);
        PermitDuration duration = createState.getDuration();

        BuildPermit newPermit = new BuildPermit(
                recipient.getAccountId(),
                recipient.getUuid(),
                issuer.getAccountId(),
                issuer.getUuid(),
                System.currentTimeMillis(),
                duration.toMs());

        WorldDef worldDef = instance.getWorldDef();

        if (duration instanceof EphemeralPermit) {
            instance.getEphemeralPermits().put(newPermit.minecraftUuid(), newPermit);
        } else {
            List<BuildPermit> persistedPermits = new ArrayList<>(worldDef.buildPermits());
            persistedPermits.removeIf(permit -> permit.isExpired(instance));
            persistedPermits.add(newPermit);
            worldDef.setPermits(persistedPermits);
            worldDef.setHasEverIssuedPermit(true);
        }

        worldDef.owner().runWithLock(Kgs.getInfra(), issuer, () -> {
            WorldDefRepo repo = Kgs.INSTANCE.getCreativeWorldsRepo().defs();
            return repo.update(worldDef).whenCompleteAsync((_, t) -> {
                if (t != null) {
                    issuer.sendPitError(MM."<gray>Couldn't create the build permit!");
                    issuer.closeInventory();
                    return;
                }

                issuer.broadcast().sendExcept(recipient, MsgCat.WORLD, NamedTextColor.GOLD, "PERMIT ISSUED!", MM."<gray>Gave \{recipient.getDisplayMM()} <gray>a build permit!",
                        SoundEvent.ITEM_GOAT_HORN_SOUND_0, 1.4);
                permitsMenu.reload().display(issuer);

                repo.removeIgnoresPermit(worldDef, recipient.getAccountId());

                KloonPlayer recipientNow = createState.getRecipient(instance);
                if (recipientNow != null) {
                    recipientNow.closeInventory();
                    recipientNow.playSound(SoundEvent.ITEM_GOAT_HORN_SOUND_0, 1.4);
                    recipientNow.sendPit(NamedTextColor.GOLD, "PERMIT ISSUED!", MM."<#F2C748>Received a build permit from \{issuer.getDisplayMM()}</#F2C748>!");
                    recipientNow.sendPit(NamedTextColor.GOLD, "BUILD PERM!", MM."<yellow>You are now allowed to edit this world!");
                }

                worldDef.broadcastInvalidate();
            });
        });
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<gold>Issue Build Permit";

        Lore lore = new Lore();
        lore.add("<dark_gray>• <green>CAN <gray>place/break blocks");
        lore.add("<dark_gray>• <green>CAN <gray>use all tools");
        lore.add("<dark_gray>• <red>CAN'T <gray>rename/edit world details");
        lore.add("<dark_gray>• <red>CAN'T <gray>load past saves");
        lore.addEmpty();
        lore.wrap("<gray>You can <red>revoke <gray>the permit at any time!");
        lore.addEmpty();

        ButtonState state = getState(player);
        lore.add(state.getCallToAction(player));

        return MenuStack.of(Material.LECTERN, name, lore);
    }

    private ButtonState getState(CreativePlayer player) {
        WorldDef worldDef = player.getInstance().getWorldDef();
        if (!worldDef.ownership().isOwner(player)) {
            return NOT_WORLD_OWNER;
        }

        int permitsCount = player.getInstance().getBuildPermits().size();
        if (permitsCount + 1 > BuildPermitsMenu.MAX_PERMITS) {
            return MAX_NUMBER_OF_PERMITS;
        }

        UUID recipientUuid = createState.getRecipientUuid();
        if (recipientUuid == null) {
            return NEED_RECIPIENT;
        }

        PermitDuration duration = createState.getDuration();
        if (duration == null) {
            return NEED_DURATION;
        }

        Player recipientPlayer = player.getInstance().getPlayerByUuid(recipientUuid);
        if (!(recipientPlayer instanceof CreativePlayer recipient)) {
            return RECIPIENT_NOT_FOUND;
        }

        if (worldDef.ownership().isOwner(recipient)) {
            return RECIPIENT_IS_OWNER;
        }

        BuildPermit existingPermit = worldDef.getPermitForPlayer(recipient.getAccountId());
        BuildPermit ephemeral = player.getInstance().getEphemeralPermits().get(recipient.getUuid());
        if (existingPermit != null || ephemeral != null) {
            return ALREADY_HAS_PERMIT;
        }

        return CAN_CREATE;
    }

    private static final ButtonState CAN_CREATE = new ButtonState("<cta>Click to issue permit!");
    private static final ButtonState NEED_RECIPIENT = new ButtonState("<!cta>Need a recipient!");
    private static final ButtonState NEED_DURATION = new ButtonState("<!cta>Need a duration!");
    private static final ButtonState RECIPIENT_NOT_FOUND = new ButtonState("<!cta>Recipient not found!");
    private static final ButtonState NOT_WORLD_OWNER = new ButtonState("<!cta>You're not the world owner!");
    private static final ButtonState ALREADY_HAS_PERMIT = new ButtonState("<!cta>Recipient already has permit!");
    private static final ButtonState RECIPIENT_IS_OWNER = new ButtonState("<!cta>Recipient is world owner!");
    private static final ButtonState MAX_NUMBER_OF_PERMITS = new ButtonState("<!cta>Reached max permits!");
}
