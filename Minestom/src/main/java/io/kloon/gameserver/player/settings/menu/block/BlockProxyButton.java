package io.kloon.gameserver.player.settings.menu.block;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.async.AsyncFetchOnClickButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.mongo.blocks.PlayerBlock;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.player.settings.menu.block.BlockProxyButton.*;

public class BlockProxyButton extends AsyncFetchOnClickButton<Data> {
    private final ChestMenu parent;

    public BlockProxyButton(ChestMenu parent) {
        this.parent = parent;
    }

    @Override
    public CompletableFuture<Data> fetchData(KloonPlayer player) {
        return Kgs.INSTANCE.getBlockRepo().getBlocks(player.getAccountId()).thenApply(Data::new);
    }

    @Override
    public void handleClickWithData(KloonPlayer player, Data data) {
        new BlocksManageMenu(parent, this, data.blocks).display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Blocked Players";

        Lore lore = new Lore();
        lore.wrap("<gray>They can't interact nor chat with you and vice versa.");
        lore.addEmpty();
        lore.add("<cta>Click to manage!");

        return MenuStack.of(Material.BRICKS, name, lore);
    }

    public record Data(
            List<PlayerBlock> blocks
    ) {}
}
