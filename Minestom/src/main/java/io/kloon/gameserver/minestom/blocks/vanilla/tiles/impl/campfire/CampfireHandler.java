package io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.campfire;

import io.kloon.gameserver.minestom.InventoryExtras;
import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.nbt.NBT;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CampfireHandler implements BlockHandler {
    public static final Key ID = Key.key("campfire");

    public static final Tag<List<ItemStack>> ITEMS = NBT.SlottedItems("Items");
    public static final List<Tag<?>> CLIENT_TAGS = Arrays.asList(ITEMS);

    @Override
    public boolean onInteract(@NotNull BlockHandler.Interaction interaction) {
        if (interaction.getHand() != PlayerHand.MAIN) return true;

        Player player = interaction.getPlayer();
        ItemStack inHand = player.getItemInMainHand();

        Instance instance = interaction.getInstance();
        Point blockPos = interaction.getBlockPosition();
        Block block = interaction.getBlock();

        FacingXZ facing = FacingXZBlock.FACING_XZ.get(block);
        Point cursorPos = interaction.getCursorPosition();
        CampfireCorner corner = computeClickedCorner(facing, cursorPos);

        CompoundBinaryTag nbt = block.nbt();
        nbt = nbt == null ? CompoundBinaryTag.empty() : nbt;
        List<ItemStack> items = ITEMS.read(nbt);
        items = items == null ? new ArrayList<>() : new ArrayList<>(items);

        ItemStack inCorner = corner.get(items);
        if (inCorner.isAir()) {
            if (inHand.isAir()) {
                return false;
            }
            corner.set(items, inHand.withAmount(1));
            InventoryExtras.consumeItemInMainHand(player);
        } else {
            boolean added = player.getInventory().addItemStack(inCorner);
            if (!added) {
                return false;
            }
            corner.set(items, ItemStack.AIR);
            player.playSound(Sound.sound(SoundEvent.ENTITY_ITEM_PICKUP, Sound.Source.PLAYER, 1f, 0.8f));
        }

        instance.setBlock(blockPos, block.withTag(ITEMS, items));

        return false;
    }

    private CampfireCorner computeClickedCorner(FacingXZ campfireFacing, Point cursorPos) {
        Vec fromCenter = Vec.fromPoint(cursorPos.sub(0.5));
        double rotation = campfireFacing.radTo(FacingXZ.WEST);
        return Arrays.stream(CampfireCorner.values())
                .max(Comparator.comparingDouble(corner -> {
                    Vec cornerDir = corner.getDirection().rotateAroundY(rotation);
                    return cornerDir.dot(fromCenter);
                }))
                .get();
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return CLIENT_TAGS;
    }

    @Override
    public @NotNull Key getKey() {
        return ID;
    }
}
