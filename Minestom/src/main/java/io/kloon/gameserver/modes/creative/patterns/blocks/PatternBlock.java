package io.kloon.gameserver.modes.creative.patterns.blocks;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.PatternType;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.security.ToolSignature;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public record PatternBlock(CreativePattern pattern) {
    public static final Tag<BinaryTag> TAG = Tag.NBT("pattern");

    public ItemStack toItem() {
        PatternType patternType = pattern.getType();
        if (pattern instanceof SingleBlockPattern single) {
            return new TinkeredBlock(single.getBlock()).toItem();
        }

        Component name = MM."\{pattern.labelMM()}";

        Lore lore = new Lore();
        lore.add(MM."<dark_gray>Block Pattern");
        lore.addEmpty();
        lore.add(pattern.lore());

        ItemBuilder2 builder = pattern.icon().name(name).lore(lore);

        byte[] bytes = MinecraftOutputStream.toBytesSneaky(pattern, CreativePattern.CODEC);
        builder.tag(TAG, ByteArrayBinaryTag.byteArrayBinaryTag(bytes));

        ItemStack stack = builder.build();
        stack = ToolSignature.signed(stack);
        return stack;
    }

    @Nullable
    public ChestMenu createEditMenu(@Nullable ChestMenu parent, ItemRef itemRef) {
        return pattern.getType().createEditMenu(parent, pattern, (player, updated) -> {
            ItemStack updatedItem = new PatternBlock(updated).toItem();
            itemRef.setIfDidntChange(updatedItem);

            //player.sendPit(TinkeredBlock.COLOR, "PATTERN BLOCK", MM."<gray>Updated!");
            //player.playSound(SoundEvent.ENTITY_AXOLOTL_SPLASH, Pitch.rng(1.8, 0.2), 0.7);
        });
    }

    @Nullable
    public static PatternBlock get(ItemStack item) {
        BinaryTag patternNbt = item.getTag(TAG);
        if (!(patternNbt instanceof ByteArrayBinaryTag binaryTag)) {
            return null;
        }

        CreativePattern pattern = MinecraftInputStream.fromBytesSneaky(binaryTag.value(), CreativePattern.CODEC);
        return new PatternBlock(pattern);
    }
}
