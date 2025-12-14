package io.kloon.gameserver.modes.creative.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.builtin.FullInventoryChange;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ClearCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(ClearCommand.class);

    public static final String LABEL = "clear";

    public ClearCommand() {
        super("clear", "clr");

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                clearPlayerInventoryExceptMenu(player);
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        });
    }

    public static void clearPlayerInventoryExceptMenu(CreativePlayer player) {
        PlayerInventory inv = player.getInventory();

        ItemStack[] invBefore = inv.getItemStacks();

        int cleared = 0;
        for (int i = 0; i < inv.getSize(); ++i) {
            ItemStack item = inv.getItemStack(i);
            if (player.getCreative().isTool(item, CreativeToolType.MENU)) {
                continue;
            }
            if (item.isAir()) {
                continue;
            }

            inv.setItemStack(i, ItemStack.AIR);
            ++cleared;
        }

        ItemStack[] invAfter = inv.getItemStacks();

        SentMessage msg = player.msg().send(MsgCat.INVENTORY,
                NamedTextColor.RED, "CLEARED!", MM."<gray>Your inventory was cleared, poof!",
                SoundEvent.ENTITY_ZOMBIE_INFECT, 1.0);

        player.addToHistory(CreativeToolType.CLEAR, "<red>Cleared inventory",
                MM."<gray>Cleared inventory of \{cleared} item(s)!", msg.sound(),
                new FullInventoryChange(invBefore, invAfter));
    }
}
