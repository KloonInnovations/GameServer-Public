package io.kloon.gameserver.modes.creative.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class DropCommand extends Command {
    public static final String LABEL = "drop";

    public DropCommand() {
        super(LABEL, "yeet");
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                byte heldSlot = player.getHeldSlot();
                PlayerInventory inventory = player.getInventory();
                ItemStack item = inventory.getItemStack(heldSlot);
                if (item.isAir()) {
                    player.msg().send(MsgCat.INVENTORY,
                            NamedTextColor.RED, "ITS' AIR!", MM."<gray>You can't drop air, because you can't hold air.",
                            SoundEvent.BLOCK_TRIPWIRE_CLICK_OFF, 1.3);
                } else {
                    player.msg().send(MsgCat.INVENTORY,
                            NamedTextColor.GREEN, "YEET!", MM."<gray>Dropped held item!",
                            SoundEvent.ITEM_BUNDLE_DROP_CONTENTS, 1.1);
                    inventory.setItemStack(heldSlot, ItemStack.AIR);
                }
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        });
    }
}
