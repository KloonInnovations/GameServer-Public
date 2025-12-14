package io.kloon.gameserver.backend;

import io.kloon.bigbackend.chat.DiscordChatMessage;
import io.kloon.bigbackend.chat.PlayerChatMessage;
import io.kloon.bigbackend.client.network.ChatClient;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.MiniMessageTemplate;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.KloonInstance;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.tablist.DefaultTablist;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTimeCooldownMap;
import io.kloon.infra.KloonNetworkInfra;
import io.kloon.infra.cache.KloonMonikerCache;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;
import io.kloon.infra.ranks.PlayerRank;
import io.kloon.infra.ranks.RankLooks;
import io.kloon.infra.util.throttle.maps.ThrottleCooldownMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ChatSync {
    private static final Logger LOG = LoggerFactory.getLogger(ChatSync.class);

    private final KloonNetworkInfra infra;
    private final ChatClient chatClient;

    private final ThrottleCooldownMap<UUID> chatThrottle = new ThrottleCooldownMap<>(2, 5_000, 5_000);
    private final ThrottleCooldownMap<UUID> floodThrottle = new ThrottleCooldownMap<>(8, 9_000, 30_000);

    public static final String DISCORD_HEX = "#AB47B0";

    public ChatSync(KloonNetworkInfra infra, ChatClient chatClient) {
        this.infra = infra;
        this.chatClient = chatClient;

        chatClient.subscribePlayerChat(MinecraftServer.getSchedulerManager(), this::onChatFromNetwork);
        chatClient.subscribeDiscordChat(MinecraftServer.getSchedulerManager(), this::onChatFromDiscord);
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        event.setCancelled(true);

        String allocationName = infra.allocationName();

        KloonPlayer sender = (KloonPlayer) event.getPlayer();
        KloonInstance instance = sender.getInstance();

        boolean throttled = sender.getRanks().isNone();
        if (throttled && !chatThrottle.get(sender.getUuid()).procIfPossible()) {
            sender.sendPit(NamedTextColor.RED, "COOLDOWN!", MM."<gray>Sorry! Your chat is on cooldown!");
            return;
        }

        if (!floodThrottle.get(sender.getUuid()).procIfPossible()) {
            sender.sendPit(NamedTextColor.RED, "THROTTLED!", MM."<gray>Your chat temporarily muted because of spam.");
            return;
        }

        PlayerChatMessage message = new PlayerChatMessage(
                sender.getUuid(),
                sender.getAccountId(),
                sender.getUsername(),
                event.getRawMessage(),
                allocationName,
                instance.getUniqueId());
        chatClient.sendPlayerChat(message);

        LOG.info(sender.getUsername() + ": " + event.getRawMessage());

        instance.streamPlayers().forEach(recipient -> {
            sendChatIfNotBlocked(recipient, sender.getAccountId(), sender.getUuid(), event.getRawMessage(), true, false);
        });
    }

    private void onChatFromNetwork(PlayerChatMessage playerChat) {
        InstanceManager instanceMan = MinecraftServer.getInstanceManager();
        instanceMan.getInstances().forEach(instance -> {
            if (!(instance instanceof KloonInstance kInstance)) {
                return;
            }

            if (instance.getUniqueId().equals(playerChat.originInstanceId())) {
                return;
            }

            kInstance.streamPlayers().forEach(recipient -> {
                boolean sameInstance = instance.getUniqueId().equals(playerChat.originInstanceId());
                sendChatIfNotBlocked(recipient, playerChat.playerId(), playerChat.minecraftUuid(), playerChat.rawMessage(), sameInstance, false);
            });
        });
    }

    private void onChatFromDiscord(DiscordChatMessage discordChat) {
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p -> {
            if (!(p instanceof KloonPlayer recipient)) return;

            ObjectId accountId = discordChat.minecraft().accountId();
            UUID minecraftUuid = discordChat.minecraft().uuid();
            sendChatIfNotBlocked(recipient, accountId, minecraftUuid, discordChat.rawMessage(), false, true);
        });
    }

    private void sendChatIfNotBlocked(KloonPlayer recipient, ObjectId senderAccountId, UUID senderUuid, String rawMessage, boolean sameInstance, boolean discord) {
        CompletableFuture<Boolean> hasBlocked = recipient.hasBlocked(senderAccountId);
        CompletableFuture<Boolean> isBlockedBy = recipient.isBlockedBy(senderAccountId);
        CompletableFuture<Component> fmtChat = isBlockedBy.thenCompose(senderBlockedRecipient -> formatChat(recipient, senderUuid, rawMessage, sameInstance, discord, senderBlockedRecipient));
        CompletableFuture.allOf(hasBlocked, fmtChat).thenRunAsync(() -> {
            if (hasBlocked.join()) {
                return;
            }

            recipient.sendMessage(fmtChat.join());
        }, recipient.scheduler());
    }

    private CompletableFuture<Component> formatChat(KloonPlayer viewer, UUID senderUuid, String rawMessage, boolean sameInstance, boolean discord, boolean senderBlockedRecipient) {
        KloonMonikerCache monikerCache = Kgs.getInfra().caches().monikers();
        return monikerCache.getByMinecraftUuid(senderUuid).thenApply(moniker -> {
            String username = moniker.minecraftUsername();

            RankLooks rankLooks = moniker.ranks().getBestRankLooks();
            String bracketsHex = discord ? DISCORD_HEX : rankLooks.nameColor().asHexString();
            String nameColor = rankLooks.nameColor().asHexString();
            String iconColor = rankLooks.iconColor().asHexString();
            String icon = rankLooks.icon();
            String chatHex = getChatHex(moniker);

            String message;
            if (senderBlockedRecipient) {
                message = "<red>[Message is blocked]";
            } else {
                message = MiniMessageTemplate.miniMessage.escapeTags(rawMessage);
            }

            Component component;
            if (icon == null) {
                component = MM."<\{nameColor}>\{username}<\{bracketsHex}>> <\{chatHex}>\{message}";
            } else {
                component = MM."<\{iconColor}>\{icon} <\{nameColor}>\{username}<\{bracketsHex}>> <\{chatHex}>\{message}";
            }

            Component hover = createHover(viewer, moniker, sameInstance, discord, senderBlockedRecipient);
            component = component.hoverEvent(hover);

            return component;
        }).exceptionally(t -> {
            LOG.error(STR."Error formatting chat \{senderUuid}", t);
            return MM."Unknown> <gray>\{rawMessage}"
                    .hoverEvent(MM."<red>Error loading player's moniker! eek!");
        });
    }

    public static String getChatHex(KloonMoniker moniker) {
        return moniker.ranks().isNone() ? "#939393" : "#EFFFFF";
    }

    private Component createHover(Player viewer, KloonMoniker moniker, boolean sameInstance, boolean discord, boolean senderBlockedRecipient) {
        Lore lore = new Lore();

        lore.add(MM."\{moniker.getDisplayMM()}");
        if (senderBlockedRecipient) {
            lore.add(MM."<red><bold>THIS PLAYER BLOCKED YOU!");
            lore.addEmpty();
        }

        boolean isSelf = viewer.getUuid().equals(moniker.minecraftUuid());
        String prefix = sameInstance
                ? STR."<\{DefaultTablist.LOCAL_HEX}>\{DefaultTablist.EARTH_AMERICA}"
                : STR."<\{DefaultTablist.REMOTE_HEX}>\{DefaultTablist.EARTH_AFRICA}";
        if (isSelf) {
            if (discord) {
                lore.add(MM."<\{DISCORD_HEX}>\uD83D\uDCAC <gray>This is you! On Discord!"); // 💬
            } else {
                lore.add(MM."<yellow>✌ <gray>This is you!");
            }
        } else if (discord) {
            lore.add(MM."<\{DISCORD_HEX}>\uD83D\uDCAC <gray>Sent from Discord!");
        } else if (sameInstance) {
            lore.add(MM."\{prefix} <gray>On your instance!");
        } else {
            lore.add(MM."\{prefix} <gray>On another instance!");
        }
        lore.addEmpty();

        PlayerRank rank = moniker.ranks().getBestRank();
        lore.add(MM."<gray>Rank: \{rank.getLooks().coloredTagMM()}");
        if (rank.isNone()) {
            lore.add(MM."<dark_gray>A regular player.");
        } else if (rank.isStaff()) {
            lore.add(MM."<dark_gray>Part of the staff team!");
        } else {
            lore.add(MM."<dark_gray>Rank from the store!");
        }

        return lore.asComponent();
    }
}
