package io.kloon.gameserver.chestmenus.builtin.async;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.infra.util.throttle.maps.ThrottleCooldownMap;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public abstract class AsyncPlayerButton<TData> implements ChestButton {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncPlayerButton.class);

    private ThrottleCooldownMap<UUID> fetchThrottle = new ThrottleCooldownMap<>(8, 35_000, 10_000);

    protected final ChestMenu parent;
    protected final int slot;
    private AsyncFetch<TData> asyncFetch; // it's only the first player who sees the button

    public AsyncPlayerButton(ChestMenu menuOfButton, int slot) {
        this.parent = menuOfButton;
        this.slot = slot;
    }

    protected void setThrottle(ThrottleCooldownMap<UUID> throttle) {
        this.fetchThrottle = throttle;
    }

    public abstract CompletableFuture<TData> fetchData(Player player);

    @Override
    public final void clickButton(Player player, ButtonClick click) {
        if (! (asyncFetch instanceof AsyncPlayerButton.FetchFuture<TData> fetching)) {
            return;
        }

        CompletableFuture<TData> getData = fetching.getData;
        if (!getData.isDone()) {
            return;
        }

        if (getData.isCancelled() || getData.isCompletedExceptionally()) {
            player.sendMessage(MM."<red>There was an error fetching data on this button!");
            return;
        }

        TData data = getData.getNow(null);
        handleClickWithData(player, click, data);
    }

    public abstract void handleClickWithData(Player player, ButtonClick click, TData data);

    @Override
    public final ItemStack renderButton(Player player) {
        if (asyncFetch == null) {
            if (fetchThrottle.get(player.getUuid()).procIfPossible()) {
                CompletableFuture<TData> getData = fetchData(player).orTimeout(2400, TimeUnit.MILLISECONDS);

                UUID playerId = player.getUuid();
                getData.whenCompleteAsync((data, fetchException) -> handlePostFetch(playerId, data, fetchException), player.scheduler()).exceptionally(t -> {
                    LOG.error("Error handling async player button post-fetch", t);
                    return null;
                });
                this.asyncFetch = new FetchFuture<>(getData);
            } else {
                this.asyncFetch = new Throttled<>();
            }
        }

        return switch (asyncFetch) {
            case AsyncPlayerButton.FetchFuture<TData> fetching -> {
                CompletableFuture<TData> getData = fetching.getData;
                if (!getData.isDone()) {
                    yield renderWhileLoading(player);
                }

                if (getData.isCancelled() || getData.isCompletedExceptionally()) {
                    yield renderOnError(player);
                }

                TData data = getData.getNow(null);
                yield renderWithData(player, data);
            }
            case AsyncPlayerButton.Throttled<TData> throttled -> renderThrottled(player);
        };
    }

    private void handlePostFetch(UUID playerId, TData data, Throwable fetchException) {
        if (fetchException != null) {
            LOG.error(STR."Error in \{getClass().getName()} async button data fetching", fetchException);
        }
        Player playerNow = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(playerId);
        ChestMenuInv chestMenuInv = ChestMenuInv.get(playerNow);
        if (chestMenuInv == null || chestMenuInv.getMenu() != parent) {
            return;
        }

        ItemStack renderedItem;
        try {
            renderedItem = renderWithData(playerNow, data);
        } catch (Throwable t) {
            LOG.error("Error in async button", t);
            renderedItem = renderOnError(playerNow);
        }

        chestMenuInv.setItemStack(slot, renderedItem);
    }

    public ItemStack renderWhileLoading(Player player) {
        return MenuStack.of(getLoadingIcon())
                .name(MM."<dark_gray>Loading...")
                .build();
    }

    public ItemStack renderThrottled(Player player) {
        return MenuStack.of(getLoadingIcon())
                .name(MM."<dark_red>Throttled!")
                .lore(MM_WRAP."<gray>Cannot fetch the data for this button, at least for a little while...")
                .build();
    }

    protected Material getLoadingIcon() {
        return Material.FEATHER;
    }

    public ItemStack renderOnError(Player player) {
        return MenuStack.of(Material.BARRIER)
                .name(MM."<red>Error!")
                .lore(MM_WRAP."There was an error while loading data for this button.")
                .build();
    }

    public abstract ItemStack renderWithData(Player player, TData data);

    private sealed interface AsyncFetch<TData> {}
    record Throttled<TData>() implements AsyncFetch<TData> {}
    record FetchFuture<TData>(CompletableFuture<TData> getData) implements AsyncFetch<TData> {}
}
