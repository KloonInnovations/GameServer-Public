package io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.decoratedpot;

import io.kloon.gameserver.minestom.InventoryExtras;
import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.BlockActionPacket;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DecoratedPotHandler implements BlockHandler {
    public static final Key ID = Key.key("decorated_pot");

    public static final Tag<List<String>> DECORATION_IDS = Tag.String("sherds").list();
    public static final List<Tag<?>> CLIENT_TAGS = Arrays.asList(DECORATION_IDS);

    @Override
    public boolean onInteract(@NotNull BlockHandler.Interaction interaction) {
        if (interaction.getHand() != PlayerHand.MAIN) return true;

        Player player = interaction.getPlayer();
        Instance instance = interaction.getInstance();
        Point blockPos = interaction.getBlockPosition();
        Block block = interaction.getBlock();

        ItemStack inHand = player.getItemInMainHand();
        if (!SherdItem.MATERIALS.contains(inHand.material())) {
            instance.sendGroupedPacket(new BlockActionPacket(blockPos, (byte) 1, (byte) 1, block));
            instance.playSound(Sound.sound(SoundEvent.BLOCK_DECORATED_POT_INSERT_FAIL, Sound.Source.BLOCK, 1f, 1f));
            return false;
        }

        InventoryExtras.consumeItemInMainHand(player);

        FacingXZ facingXZ = FacingXZBlock.FACING_XZ.get(block);

        CompoundBinaryTag nbt = block.nbt();
        nbt = nbt == null ? CompoundBinaryTag.empty() : nbt;

        List<String> decorations = DECORATION_IDS.read(nbt);
        decorations = decorations == null
                ? new ArrayList<>(Collections.nCopies(4, Material.BRICK.name()))
                : new ArrayList<>(decorations);

        int faceIndex = getClickedFaceIndex(facingXZ, interaction.getBlockFace());
        decorations.set(faceIndex, inHand.material().name());

        block = block.withTag(DECORATION_IDS, decorations);
        instance.setBlock(blockPos, block);

        instance.sendGroupedPacket(new BlockActionPacket(blockPos, (byte) 1, (byte) 0, block));
        instance.playSound(Sound.sound(SoundEvent.BLOCK_DECORATED_POT_INSERT, Sound.Source.BLOCK, 1f, 1f));

        return false;
    }

    private int getClickedFaceIndex(FacingXZ blockFacing, BlockFace clickedFace) {
        RotationTransform transform = blockFacing.transformTo(FacingXZ.SOUTH);
        BlockFace face = transform.rotate(clickedFace.getOppositeFace());

        return switch (face) {
            case NORTH -> 0;
            case WEST -> 1;
            case EAST -> 2;
            default -> 3;
        };
    }

    private BlockFace clockwise(BlockFace face) {
        return DirectionUtils.clockwise(face);
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
