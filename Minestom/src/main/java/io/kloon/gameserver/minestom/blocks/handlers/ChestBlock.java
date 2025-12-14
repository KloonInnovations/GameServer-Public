package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.ChestType;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ChestBlock extends FacingXZBlock implements BlockHandler {
    public static final EnumProp<ChestType> TYPE = new EnumProp<>("type", ChestType.class);
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;

    public static final Set<Block> BLOCKS = Sets.newHashSet(Block.CHEST, Block.TRAPPED_CHEST);

    private final Block block;

    public ChestBlock(Block block) {
        this.block = block;
    }

    @Override
    public @NotNull Key getKey() {
        return block.key();
    }
}
