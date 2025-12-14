package io.kloon.gameserver.service;

import io.kloon.bigbackend.BackendTopics;
import io.kloon.bigbackend.events.InvalidateMonikerEvent;
import io.kloon.bigbackend.events.StorePurchaseEvent;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.KloonGameServer;
import io.kloon.gameserver.minestom.scheduler.Repeat;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.cache.KloonMonikerCache;
import io.kloon.infra.ranks.StoreRank;
import io.kloon.infra.serviceframework.NatsService;
import io.kloon.infra.serviceframework.annotations.Sub;
import io.kloon.infra.store.KloonStore;
import io.kloon.infra.store.products.EarlyAdopterRank;
import io.kloon.infra.store.products.KloonProduct;
import io.kloon.infra.store.products.ProSubscription;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class GameServerService extends NatsService {
    private static final Logger LOG = LoggerFactory.getLogger(GameServerService.class);

    private final KloonGameServer gameServer;

    public GameServerService(KloonGameServer gameServer) {
        super(gameServer.getInfra().nats(), MinecraftServer.getSchedulerManager());
        this.gameServer = gameServer;
    }

    @Sub(topic = BackendTopics.INVALIDATE_MONIKER)
    public void invalidateMoniker(InvalidateMonikerEvent event) {
        int spreadLoadDelay = ThreadLocalRandom.current().nextInt(3 * 20);
        gameServer.getScheduler().scheduleTask(() -> {
            KloonMonikerCache monikerCache = gameServer.getInfra().caches().monikers();
            monikerCache.invalidateLocal(event.accountId());
            monikerCache.invalidateLocal(event.minecraftUuid());
        }, TaskSchedule.tick(spreadLoadDelay), TaskSchedule.stop());
    }

    @Sub(topic = BackendTopics.STORE_PURCHASE)
    public void onStorePurchase(StorePurchaseEvent event) {
        gameServer.getAccountsRepo().moniker().get(event.accountId()).thenAcceptAsync(moniker -> {
            if (moniker == null) {
                LOG.warn(STR."Received a store purchase for unknown account \{event.accountIdHex()}");
                return;
            }

            KloonStore store = KloonStore.get(gameServer.getInfra().environment());
            KloonProduct product = store.getProductByDbKey(event.productDbKey());

            MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> {
                KloonPlayer kp = (KloonPlayer) player;
                Component msg = switch (product) {
                    case EarlyAdopterRank _ -> MM."\{moniker.getDisplayMM()} <gray>upgraded to \{StoreRank.EARLY_ADOPTER.getLooks().coloredTagMM()}<gray>!";
                    case ProSubscription _ -> MM."\{moniker.getDisplayMM()} <gray>subscribed to \{StoreRank.PRO.getLooks().coloredTagMM()}<gray>!";
                    case null -> MM."\{moniker.getDisplayMM()} <gray>got something secret from the store!";
                };
                msg = msg
                        .hoverEvent(MM."<cta>Click to visit the store!")
                        .clickEvent(ClickEvent.openUrl("https://kloon.io/store"));
                kp.sendPit(TextColor.color(255, 38, 110), "KLOON.IO STORE!", msg);
            });

            Player buyerPlayer = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(moniker.minecraftUuid());
            if (buyerPlayer instanceof KloonPlayer buyer) {
                Repeat.n(buyer.scheduler(), 20, 1, t -> {
                    buyer.playSound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, 0.7 + t * 0.04, 0.4);
                    buyer.playSound(SoundEvent.BLOCK_NOTE_BLOCK_CHIME, 1.1 + t * 0.02, 0.6);
                    buyer.playSound(SoundEvent.ENTITY_PLAYER_LEVELUP, 0.7 + t * 0.05, 0.6);
                });
                buyer.scheduleTicks(() -> {
                    buyer.playSound(SoundEvent.ITEM_GOAT_HORN_SOUND_1, 1.8);
                }, 21);
                buyer.sendMessage(MM."<#FF266E><st>-----------------------------");
                buyer.sendMessage(MM." <gold>\uD83C\uDFC6 <#FF266E><b>THANK YOU SO MUCH FOR YOUR SUPPORT!"); // 🏆
                buyer.sendMessage(MM." <yellow>You may need to re-log to claim all your benefits!");
                buyer.sendMessage(MM." <gray>You received this following a purchase on the web store.");
                buyer.sendMessage(MM."<#FF266E><st>-----------------------------");
            }
        }, MinecraftServer.getSchedulerManager()).exceptionally(t -> {
            LOG.error("Error with store purchase notification", t);
            return null;
        });
    }
}
