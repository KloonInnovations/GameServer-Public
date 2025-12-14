package io.kloon.gameserver.player.settings.menu.block;

import humanize.Humanize;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.async.AsyncPlayerButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.commands.player.block.UnblockCommand;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.formatting.TimeFmt;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;
import io.kloon.infra.mongo.blocks.PlayerBlock;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.player.settings.menu.block.BlockedPlayerButton.*;

public class BlockedPlayerButton extends AsyncPlayerButton<Data> {
    private final BlockProxyButton blockProxy;
    private final PlayerBlock block;

    public BlockedPlayerButton(ChestMenu menuOfButton, int slot, BlockProxyButton blockProxy, PlayerBlock block) {
        super(menuOfButton, slot);
        this.blockProxy = blockProxy;
        this.block = block;
    }

    @Override
    public CompletableFuture<Data> fetchData(Player player) {
        CompletableFuture<KloonMoniker> getMoniker = Kgs.getCaches().monikers().getByAccountId(block.getTargetId());
        CompletableFuture<HeadProfile> getHead = getMoniker.thenCompose(SkinCache::get);
        return CompletableFuture.allOf(getMoniker, getHead).thenApply(_ -> new Data(getMoniker.join(), getHead.join()));
    }

    @Override
    public void handleClickWithData(Player p, ButtonClick click, Data data) {
        KloonPlayer player = (KloonPlayer) p;
        String username = data.moniker.minecraftUsername();
        UnblockCommand.unblock(player, username).thenRunAsync(() -> {
            blockProxy.fetchAndHandleClick(player);
        }, player.scheduler());
    }

    @Override
    public ItemStack renderWithData(Player player, Data data) {
        KloonMoniker moniker = data.moniker;

        Component name = MM."\{moniker.getDisplayMM()}";

        Lore lore = new Lore();
        lore.addEmpty();
        lore.add(MM."<gray>Blocked: <red>\{Humanize.naturalTime(new Date(block.getTimestamp()))}");
        lore.addEmpty();
        lore.add("<cta>Click to unblock!");

        return MenuStack.of(Material.PLAYER_HEAD)
                .set(DataComponents.PROFILE, data.head)
                .name(name)
                .lore(lore)
                .build();
    }

    public record Data(
            KloonMoniker moniker,
            HeadProfile head
    ) {}
}
