package io.kloon.gameserver.modes.creative.commands.masks;

import io.kloon.gameserver.minestom.armor.ArmorFamily;
import io.kloon.gameserver.minestom.armor.ArmorSlot;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class UndressCommand extends Command {
    public static final String LABEL = "undress";

    public UndressCommand() {
        super(LABEL);
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                undress(player);
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        });
    }

    public static void undress(CreativePlayer player) {
        List<ArmorSlot> slotsToUndress = new ArrayList<>(4);

        for (ArmorSlot slot : ArmorSlot.values()) {
            ItemStack item = slot.get(player);
            if (!item.isAir()) {
                slotsToUndress.add(slot);
            }
        }

        if (slotsToUndress.isEmpty()) {
            player.sendPit(NamedTextColor.DARK_PURPLE, "WEARING NOTHING!", MM."<gray>You aren't wearing any armor pieces!");
            player.playSound(SoundEvent.ENTITY_VILLAGER_NO, Pitch.rng(0.62, 0.1));
            return;
        }

        List<SoundEvent> sounds = new ArrayList<>(slotsToUndress.size());
        for (ArmorSlot slot : slotsToUndress) {
            ItemStack armorPiece = slot.get(player);
            if (player.getInventoryExtras().isFull()) {
                player.sendPit(NamedTextColor.RED, "NO ROOM!", MM."<gray>In your inventory for your \{slot}!");
                sounds.add(SoundEvent.ENTITY_VILLAGER_HURT);
                continue;
            }

            slot.set(player, ItemStack.AIR);
            player.getInventoryExtras().addItemReverse(armorPiece);

            Component itemName = BlockFmt.getName(armorPiece);
            player.sendPit(NamedTextColor.DARK_PURPLE, STR."\{slot.getName()}", MM."<gray>Worn ".append(itemName).append(MM."<gray> moved to inventory!"));

            ArmorFamily armorFamily = ArmorFamily.get(armorPiece);
            SoundEvent sound = armorFamily == null ? SoundEvent.ITEM_ARMOR_EQUIP_GENERIC : armorFamily.equipSound();
            sounds.add(sound);
        }

        double basePitch = 0.6 + ThreadLocalRandom.current().nextDouble(0.12);
        for (int i = 0; i < sounds.size(); ++i) {
            SoundEvent sound = sounds.get(i);
            double pitch = basePitch + 0.082 * i;
            player.scheduleTicks(() -> player.playSound(sound, pitch), i * 2);
        }
    }
}
