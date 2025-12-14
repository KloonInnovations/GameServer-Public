package io.kloon.gameserver.modes.creative.commands.enderchest;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestItem;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class EnderChestSaveCommand extends Command {
    public static final String LABEL = "save";

    public EnderChestSaveCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                ItemStack inHand = player.getItemInMainHand();
                if (inHand.isAir()) {
                    player.playSound(SoundEvent.ENTITY_ARMADILLO_HURT, Pitch.rng(1.5, 0.3));
                    player.sendPit(NamedTextColor.RED, "SAVE WHAT?", MM."<gray>You aren't holding an item in your hand!");
                    return;
                }

                saveItem(player, inHand);
            }
        });
    }

    public static boolean saveItem(CreativePlayer player, ItemStack item) {
        EnderChestStorage echest = player.getEnderChest();
        if (echest.hasItemStack(item)) {
            player.playSound(SoundEvent.ENTITY_ARMADILLO_HURT, Pitch.rng(1.5, 0.3));
            player.sendPit(NamedTextColor.RED, "WOAH THERE!", MM."<gray>You already have an item exactly like that!");
            return false;
        }

        int limit = EnderChestStorage.ITEMS_LIMIT;
        if (echest.getItems().size() >= limit) {
            player.playSound(SoundEvent.ENTITY_ARMADILLO_HURT, Pitch.rng(1.5, 0.3));
            player.sendPit(NamedTextColor.RED, "IT'S FULL!", MM."<gray>Reached the <dark_purple>\{limit} items <gray>limit in the ender chest!");
            return false;
        }

        if (!echest.checkActionCooldown()) {
            return false;
        }

        EnderChestItem echestItem = new EnderChestItem(new ObjectId(), player.getAccountId(), item);
        echest.save(echestItem);

        Component name = echestItem.getName();
        player.playSound(SoundEvent.ENTITY_ARMADILLO_ROLL, 1f);
        player.sendPit(NamedTextColor.GREEN, "SAVED!", MM."<gray>Saved ".append(name).append(MM."<gray> to your ender chest!"));
        return true;
    }
}
