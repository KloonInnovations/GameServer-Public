package io.kloon.gameserver.minestom.blocks.vanilla.interactions.impl;

import io.kloon.gameserver.minestom.blocks.handlers.CauldronBlock;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaBlockInteraction;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.PotionContents;
import net.minestom.server.potion.PotionType;
import net.minestom.server.sound.SoundEvent;

public class CauldronVanillaInteraction implements VanillaBlockInteraction {
    private static final IntProp LEVEL = CauldronBlock.LEVEL;

    public static final ItemStack WATER_BOTTLE = new ItemBuilder2(Material.POTION)
            .set(DataComponents.POTION_CONTENTS, new PotionContents(PotionType.WATER))
            .build();

    @Override
    public boolean handleInteract(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        if (block.defaultState() == Block.CAULDRON) {
            return handleEmptyCauldron(player, instance, blockPos, cursorPos, block);
        } else if (CauldronBlock.BLOCKS_SET.contains(block.defaultState())) {
            ItemStack inHand = player.getItemInMainHand();
            if (inHand.material() == Material.BUCKET) {
                return handleDrainWithBucket(player, instance, blockPos, cursorPos, block);
            } else if (inHand.material() == Material.GLASS_BOTTLE) {
                return handleDrainWithBottle(player, instance, blockPos, cursorPos, block);
            } else {
                return handleFilling(player, instance, blockPos, cursorPos, block);
            }
        }
        return false;
    }

    private boolean handleEmptyCauldron(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        ItemStack inHand = player.getItemInMainHand();
        Material mat = inHand.material();
        Block computedBlock = null;
        if (mat == Material.WATER_BUCKET) {
            computedBlock = LEVEL.get(3).on(Block.WATER_CAULDRON);
            player.playSound(Sound.sound(SoundEvent.ITEM_BUCKET_EMPTY, Sound.Source.BLOCK, 1f, 1f));
            player.setItemInMainHand(ItemStack.of(Material.BUCKET));
        } else if (isWaterBottle(inHand)) {
            computedBlock = LEVEL.get(1).on(Block.WATER_CAULDRON);
            player.playSound(Sound.sound(SoundEvent.ITEM_BOTTLE_EMPTY, Sound.Source.BLOCK, 1f, 1f));
            player.setItemInMainHand(ItemStack.of(Material.GLASS_BOTTLE));
        } else if (mat == Material.LAVA_BUCKET) {
            computedBlock = Block.LAVA_CAULDRON;
            player.playSound(Sound.sound(SoundEvent.ITEM_BUCKET_EMPTY_LAVA, Sound.Source.BLOCK, 1f, 1f));
            player.setItemInMainHand(ItemStack.of(Material.BUCKET));
        } else if (mat == Material.POWDER_SNOW_BUCKET) {
            computedBlock = LEVEL.get(1).on(Block.POWDER_SNOW_CAULDRON);
            player.playSound(Sound.sound(SoundEvent.ITEM_BUCKET_EMPTY_POWDER_SNOW, Sound.Source.BLOCK, 1f, 1f));
            player.setItemInMainHand(ItemStack.of(Material.BUCKET));
        }

        if (computedBlock == null) {
            return false;
        }

        instance.setBlock(blockPos, computedBlock);

        return true;
    }

    private boolean handleFilling(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        ItemStack inHand = player.getItemInMainHand();
        Material mat = inHand.material();

        Block computedBlock = null;
        if (block.defaultState() == Block.WATER_CAULDRON) {
            if (mat == Material.WATER_BUCKET) {
                computedBlock = LEVEL.get(3).on(block);
                player.playSound(Sound.sound(SoundEvent.ITEM_BUCKET_EMPTY, Sound.Source.BLOCK, 1f, 1f));
                player.setItemInMainHand(ItemStack.of(Material.BUCKET));
            } else if (isWaterBottle(inHand)) {
                int level = LEVEL.get(block) + 1;
                if (level < LEVEL.getMaxExcluded()) {
                    computedBlock = LEVEL.get(level).on(Block.WATER_CAULDRON);
                    player.playSound(Sound.sound(SoundEvent.ITEM_BOTTLE_EMPTY, Sound.Source.BLOCK, 1f, 1f));
                    player.setItemInMainHand(ItemStack.of(Material.GLASS_BOTTLE));
                } else {
                    return true;
                }
            }
        } else if (block.defaultState() == Block.POWDER_SNOW_CAULDRON && mat == Material.POWDER_SNOW_BUCKET) {
            int level = LEVEL.get(block) + 1;
            if (level < LEVEL.getMaxExcluded()) {
                computedBlock = LEVEL.get(level).on(Block.POWDER_SNOW_CAULDRON);
                player.playSound(Sound.sound(SoundEvent.ITEM_BUCKET_EMPTY_POWDER_SNOW, Sound.Source.BLOCK, 1f, 1f));
                player.setItemInMainHand(ItemStack.of(Material.BUCKET));
            } else {
                return true;
            }
        }

        if (computedBlock == null) {
            return false;
        }

        instance.setBlock(blockPos, computedBlock);
        return true;
    }

    private boolean handleDrainWithBucket(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        int level = LEVEL.get(block);

        if (block.defaultState() == Block.WATER_CAULDRON) {
            player.setItemInMainHand(ItemStack.of(Material.WATER_BUCKET));
            player.playSound(Sound.sound(SoundEvent.ITEM_BUCKET_FILL, Sound.Source.BLOCK, 1f, 1f));
            instance.setBlock(blockPos, Block.CAULDRON);
        } else if (block.defaultState() == Block.LAVA_CAULDRON) {
            player.setItemInMainHand(ItemStack.of(Material.LAVA_BUCKET));
            player.playSound(Sound.sound(SoundEvent.ITEM_BUCKET_FILL_LAVA, Sound.Source.BLOCK, 1f, 1f));
            instance.setBlock(blockPos, Block.CAULDRON);
        } else if (block.defaultState() == Block.POWDER_SNOW_CAULDRON) {
            player.setItemInMainHand(ItemStack.of(Material.POWDER_SNOW_BUCKET));
            player.playSound(Sound.sound(SoundEvent.ITEM_BUCKET_FILL_POWDER_SNOW, Sound.Source.BLOCK, 1f, 1f));
            if (level > 1) {
                instance.setBlock(blockPos, LEVEL.get(level - 1).on(block));
            } else {
                instance.setBlock(blockPos, Block.CAULDRON);
            }
        } else {
            return false;
        }

        return true;
    }

    private boolean handleDrainWithBottle(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        int level = LEVEL.get(block);
        if (level == 0) return true;

        if (block.defaultState() == Block.WATER_CAULDRON) {
            if (level > 1) {
                instance.setBlock(blockPos, LEVEL.get(level - 1).on(block));
            } else {
                instance.setBlock(blockPos, Block.CAULDRON);
            }
            player.setItemInMainHand(WATER_BOTTLE);
            player.playSound(Sound.sound(SoundEvent.ITEM_BOTTLE_FILL, Sound.Source.BLOCK, 1f, 1f));
        }

        return true;
    }

    private boolean isWaterBottle(ItemStack item) {
        if (item.material() != Material.POTION) {
            return false;
        }

        PotionContents contents = item.get(DataComponents.POTION_CONTENTS);
        return contents == null || contents.potion() == PotionType.WATER;
    }
}
