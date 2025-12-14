package io.kloon.gameserver.commands.testing;

import io.kloon.bigbackend.BackendTopics;
import io.kloon.bigbackend.events.StorePurchaseEvent;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.mongo.accounts.KloonAccount;
import io.kloon.infra.serviceframework.NatsClient;
import io.kloon.infra.store.KloonStore;
import io.nats.client.Connection;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class FakeBuyCommand extends AdminCommand {
    public FakeBuyCommand() {
        super("fakebuy");
        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                FakeBuyClient fakeBuyClient = new FakeBuyClient(Kgs.getInfra().nats());
                KloonStore store = KloonStore.get(Kgs.getInfra().environment());

                fakeBuyClient.broadcastStorePurchase(player.getAccount(), store.proYearly().dbKey());

                player.sendMessage("Sent fake buy broadcast.");
            }
        });
    }

    private static class FakeBuyClient extends NatsClient {
        public FakeBuyClient(Connection nats) {
            super(nats);
        }

        public void broadcastStorePurchase(KloonAccount account, String productDbKey) {
            StorePurchaseEvent packet = new StorePurchaseEvent(account.getId().toHexString(), productDbKey);
            publishJson(BackendTopics.STORE_PURCHASE, packet);
        }
    }
}
