package io.kloon.gameserver.modes.creative.commands.masks;

import io.kloon.gameserver.minestom.armor.ArmorSlot;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.history.builtin.ArmorInventoryChange;
import io.kloon.gameserver.modes.creative.history.builtin.FullInventoryChange;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ClearMasksCommand extends Command {
    public static final String LABEL = "clearmasks";

    public ClearMasksCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                clearMasks(player);
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        });
    }

    public static void clearMasks(CreativePlayer player) {
        ItemStack[] armorBefore = ArmorInventoryChange.getArmor(player);
        int cleared = 0;
        for (ArmorSlot slot : ArmorSlot.VALUES) {
            if (slot.get(player).isAir()) continue;
            slot.set(player, ItemStack.AIR);
            ++cleared;
        }
        ItemStack[] armorAfter = ArmorInventoryChange.getArmor(player);

        CoolSound sound = player.playSound(SoundEvent.ENTITY_ZOMBIE_INFECT, 1.3);
        if (cleared == 0) {
            player.msg().send(MsgCat.INVENTORY, NamedTextColor.RED, "CLEARED!", MM."<gray>Your equipped armor was super-cleared!");
            return;
        }

        player.msg().send(MsgCat.INVENTORY, NamedTextColor.RED, "CLEARED!", MM."<gray>Your equipped armor was cleared, zoop!");

        player.addToHistory(CreativeToolType.CLEAR, "<red>Cleared armor",
                MM."<gray>Cleared armor of \{cleared} pieces(s)!", sound,
                new ArmorInventoryChange(armorBefore, armorAfter));
    }
}
