package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.SkullBlock;
import io.kloon.gameserver.minestom.blocks.properties.BannerRotProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.nbt.NBT;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.codec.Transcoder;
import net.minestom.server.component.DataComponents;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkullPlacement extends BlockPlacementRule {
    public SkullPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        BlockFace face = state.blockFace();
        if (face == null) {
            return state.block();
        }

        float yaw = state.playerPosition().yaw();

        Block block;
        if (face == BlockFace.TOP || face == BlockFace.BOTTOM) {
            int segment = BannerRotProp.convertToSegment(yaw);
            block = SkullBlock.GROUND_ROTATION.get(segment).on(state);
        } else {
            FacingXZ facing = FacingXZ.fromBlockFace(face);
            Block wallBlock = SkullBlock.BLOCK_TO_WALL.get(state.block().defaultState());
            block = SkullBlock.WALL_FACING.get(facing).on(wallBlock);
        }

        ItemStack itemStack = state.usedItemStack();
        if (itemStack != null && itemStack.material() == Material.PLAYER_HEAD && block.id() == Block.PLAYER_HEAD.id()) {
            HeadProfile profile = itemStack.get(DataComponents.PROFILE);
            if (profile != null) {
                BinaryTag profileNbt = HeadProfile.CODEC.encode(Transcoder.NBT, profile).orElseThrow();
                CompoundBinaryTag nbt = NBT.compound(c -> {
                    c.put("profile", profileNbt);
                });
                block = block.withNbt(nbt);
            }
        }

        return block;
    }
}
