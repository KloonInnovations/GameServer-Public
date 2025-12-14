package io.kloon.gameserver.commands.player;

import io.kloon.bigbackend.transfers.TransferInstance;
import io.kloon.bigbackend.transfers.TransferPlayer;
import io.kloon.bigbackend.transfers.TransferSlot;
import io.kloon.bigbackend.transfers.join.JoinTransferReply;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.allocation.AllocateSlotReply;
import io.kloon.gameserver.allocation.AllocateSlotRequest;
import io.kloon.gameserver.client.GameServerClient;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.commands.executors.VirtualExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.service.allocations.approved.ApprovedTransfer;
import io.kloon.gameserver.service.allocations.approved.JoinApprovedTransfer;
import io.kloon.infra.cache.MinecraftUuidCache;
import io.kloon.infra.mongo.accounts.KloonAccount;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class JoinCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(JoinCommand.class);

    public static final String LABEL = "join";

    public JoinCommand() {
        super(LABEL);

        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                player.sendMessage(MM."<red>Usage: /join <username>");
            }
        });

        Argument<String> nameArg = ArgumentType.String("username");
        addSyntax(new VirtualExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                String targetUsername = context.get(nameArg).toLowerCase();
                Player targetOnInstance = sync(() -> player.getInstance().getPlayers().stream()
                        .filter(p -> p.getUsername().equalsIgnoreCase(targetUsername))
                        .findFirst().orElse(null)).join();
                if (targetOnInstance != null) {
                    player.playSound(SoundEvent.ENTITY_CAT_BEG_FOR_FOOD, 1.0);
                    if (targetOnInstance == player) {
                        player.sendPit(NamedTextColor.GREEN, "HEY NOW!", MM."<gray>You cannot /join yourself, at least until cloning gets popular.");
                    } else {
                        player.sendPit(NamedTextColor.GREEN, "HEYO!", MM."<gray>This player is on the same instance as you!");
                    }
                    return;
                }

                MinecraftUuidCache uuidsCache = Kgs.getInfra().caches().uuids();
                UUID targetUuid = uuidsCache.getByUsername(targetUsername).join();
                KloonAccount targetAccount = targetUuid == null ? null : Kgs.getAccountsRepo().get(targetUuid).join();
                if (targetUuid == null || targetAccount == null) {
                    player.playSound(SoundEvent.ENTITY_CAT_BEG_FOR_FOOD, 1.0);
                    player.sendPit(NamedTextColor.RED, "NOT FOUND!", MM."<gray>We haven't heard of a player with that username!");
                    return;
                }

                player.sendMessage(MM."<gray>Requesting to join \{targetUsername}...");

                TransferPlayer requester = player.toTransferPlayer();
                JoinTransferReply joinReply = Kgs.getBackend().getTransfers().joinTransfer(requester, targetUuid).join();
                TransferInstance transferInstance = joinReply.instance();
                JoinTransferReply.Status findJoinServerStatus = joinReply.status();
                if (findJoinServerStatus != JoinTransferReply.Status.OK || transferInstance == null) {
                    player.playSound(SoundEvent.ENTITY_CAT_BEG_FOR_FOOD, 1.0);
                    switch (findJoinServerStatus) {
                        case TARGET_NOT_FOUND -> player.sendPit(NamedTextColor.RED, "NOT FOUND!", MM."<gray>Couldn't find that player! Are they online?");
                        case null, default -> player.sendPit(NamedTextColor.RED, "UH OH!", MM."<gray>Couldn't join player because: <red>\{findJoinServerStatus}");
                    }
                    return;
                }

                GameServerClient client = new GameServerClient(Kgs.getInfra().nats(), transferInstance.serverAllocation());

                AllocateSlotRequest slotReq = new AllocateSlotRequest(UUID.randomUUID(), player.getAccountId(), player.getUuid(), transferInstance.instanceId())
                        .withJoin(new AllocateSlotRequest.PlayerJoin(targetUuid));
                AllocateSlotReply allocateSlotReply = client.allocateSlot(slotReq).join();
                AllocateSlotReply.Status allocateSlotStatus = allocateSlotReply.getStatus();
                if (allocateSlotStatus != AllocateSlotReply.Status.OK) {
                    player.playSound(SoundEvent.ENTITY_CAT_BEG_FOR_FOOD, 1.0);
                    switch (allocateSlotStatus) {
                        case DENIED_CUSTOM -> player.sendPit(NamedTextColor.RED, "CAN'T JOIN", MM."<gray>You cannot join the instance that player is on!");
                        case SERVER_IS_FULL -> player.sendPit(NamedTextColor.RED, "FULL!", MM."<gray>The instance that player is on is full!");
                        case JOIN_PLAYER_NOT_ON_INSTANCE -> player.sendPit(NamedTextColor.RED, "OOPS", MM."<gray>Couldn't quite find that player! Please try again!");
                        case JOIN_PLAYER_HAS_DISABLED_THE_SETTING -> player.sendPit(NamedTextColor.RED, "DENIED!", MM."<gray>This player has disabled receiving /joins!");
                        case JOIN_WORLD_IS_DISABLED -> player.sendPit(NamedTextColor.RED, "DEDNIED!", MM."<gray>The world the player is on has /joins disabled!");
                        case TARGET_BLOCKED_REQUESTER -> player.sendPit(NamedTextColor.RED, "BLOCKED!", MM."<gray>Can't /join a player who blocked you.");
                        case null, default -> player.sendPitError(MM."<red>Couldn't transfer to server and we don't know why!");
                    }
                    return;
                }

                TransferSlot transferSlot = new TransferSlot(slotReq.slotId(),
                        transferInstance.serverAllocation(), transferInstance.serverDisplayName(), transferInstance.instanceId(),
                        player.getUuid(), System.currentTimeMillis() + 3000); // TODO: Eek

                JoinApprovedTransfer approvedTransfer = new JoinApprovedTransfer(transferSlot, targetUuid);
                sync(() -> player.executeTransfer(approvedTransfer));
            }
        }, nameArg);
    }
}