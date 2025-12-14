package io.kloon.gameserver.chestmenus.builtin.async;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.ChestButtonCooldown;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class AsyncFetchOnClickButton<TData> implements ChestButton {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncFetchOnClickButton.class);
    
    private final ChestButtonCooldown cooldown = new ChestButtonCooldown();

    @Override
    public final void clickButton(Player player, ButtonClick click) {
        if (!cooldown.check(player)) {
            return;
        }

        fetchAndHandleClick(player);
    }

    public final void fetchAndHandleClick(Player player) {
        if (!(player instanceof KloonPlayer kp)) {
            return;
        }

        CompletableFuture<TData> fetchData = fetchData(kp);
        fetchData.whenCompleteAsync((data, t) -> {
            try {
                if (t != null) {
                    onDataFetchError(kp, t);
                    return;
                }

                handleClickWithData(kp, data);
            } catch (Throwable t2) {
                LOG.error("Error handling async button click", t2);
            }
        }, player.scheduler());
    }

    public abstract CompletableFuture<TData> fetchData(KloonPlayer player);

    public void onDataFetchError(KloonPlayer player, Throwable t) {
        player.sendMessage(MM."<red>Error while fetching data for a menu button!");
        player.closeInventory();

        LOG.error(STR."Error in \{getClass().getName()} async button data fetching", t);
    }

    public abstract void handleClickWithData(KloonPlayer player, TData data);
}
