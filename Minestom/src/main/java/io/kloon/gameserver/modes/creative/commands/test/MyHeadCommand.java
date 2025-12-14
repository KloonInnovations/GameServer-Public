package io.kloon.gameserver.modes.creative.commands.test;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MyHeadCommand extends AdminCommand {
    public MyHeadCommand() {
        super("myhead");
        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                HeadProfile profile = SkinCache.get(player);
                ItemStack item = ItemStack.builder(Material.PLAYER_HEAD)
                        .set(DataComponents.PROFILE, profile)
                        .build();
                player.getInventory().addItemStack(item);
                player.sendMessage(MM."<yellow>Add your own head to your inventory!");
            }
        });
    }
}
