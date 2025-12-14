package io.kloon.gameserver.modes.creative.commands.masks.api;

import io.kloon.gameserver.minestom.armor.ArmorFamily;
import io.kloon.gameserver.minestom.armor.ArmorSlot;
import io.kloon.gameserver.minestom.scheduler.Repeat;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.armorpicker.PlayerArmorPicker;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class MaskItemExecutor<Data> extends CreativeExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(MaskItemExecutor.class);

    private final MaskType<Data> maskType;

    public MaskItemExecutor(MaskType<Data> maskType) {
        this.maskType = maskType;
    }

    @Override
    public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
        if (!player.canEditWorld()) {
            player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>You cannot spawn/edit this mask without edit permissions!");
            return;
        }

        ItemStack inHand = player.getItemInMainHand();
        MaskItem maskItem = MaskItem.get(inHand);
        if (maskItem == null) {
            give(player, context);
        } else {
            addToItem(player, maskItem, ItemRef.mainHand(player), context);
        }
    }

    public void give(CreativePlayer player, CommandContext context) {
        ArmorSlot armorSlot = player.getInventoryExtras().getFreeArmorSlot();

        ItemStack maskStack;
        try {
            MaskWithData<Data> maskWithData = maskType.createDefault();
            modifyData(player, maskWithData.data(), context);

            if (armorSlot == null) {
                maskType.giveToPlayer(player, maskWithData);
                return;
            }

            Material material = ArmorFamily.LEATHER.get(armorSlot);
            Color armorColor = MaskItem.generateRandomArmorColor();
            MaskItem maskItem = new MaskItem(material, armorColor, MaskItem.DEFAULT_UNION, Collections.singletonList(maskWithData));
            maskStack = maskItem.renderItem();
        } catch (Throwable t) {
            LOG.error("Error modifying mask data for command!", t);
            player.sendPitError(MM."<gray>While running this mask command!");
            return;
        }

        armorSlot.set(player, maskStack);

        ArmorFamily armorFamily = ArmorFamily.get(maskStack);
        if (armorFamily != null) {
            player.playSound(armorFamily.equipSound(), 1.0);
        }
        player.msg().send(MsgCat.INVENTORY,
                NamedTextColor.GREEN, "MASK!", MM."\{maskType.getNameMM()} <gray>mask equipped in \{armorSlot.getNameMM().toLowerCase()} <gray>slot!");
    }

    public void addToItem(CreativePlayer player, MaskItem maskItem, ItemRef itemRef, CommandContext context) {
        if (maskItem.getMasks().stream().anyMatch(mask -> mask.type() == maskType)) {
            player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>Held wearable already has a mask of this type!");
            player.playSound(SoundEvent.BLOCK_BEEHIVE_EXIT, 1.4);
            return;
        }

        ItemStack maskStack;
        try {
            MaskWithData<Data> maskWithData = maskType.createDefault();
            modifyData(player, maskWithData.data(), context);
            maskItem = maskItem.withAddedMask(maskWithData);
            maskStack = maskItem.renderItem();
        } catch (Throwable t) {
            LOG.error("Error modifying mask data for command!", t);
            player.sendPitError(MM."<gray>While running this mask command!");
            return;
        }

        boolean changed = itemRef.setIfDidntChange(maskStack);
        if (!changed) {
            player.playSound(SoundEvent.ENTITY_ENDERMAN_TELEPORT, 0.6);
            player.sendPit(NamedTextColor.RED, "OOPS", MM."<gray>Couldn't edit your mask, somehow!");
            return;
        }

        Repeat.n(player.scheduler(), 3, 3, t -> player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_CHIME, Pitch.rng(0.8, 1.0)));
        player.msg().send(MsgCat.INVENTORY,
                NamedTextColor.GREEN, "MASK", MM."\{maskType.getNameMM()} <gray>mask added to held wearable!");
    }

    public abstract void modifyData(CreativePlayer player, Data data, CommandContext context);
}
