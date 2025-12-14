package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.placement.generic.LooksFacingXZPlacement;
import io.kloon.gameserver.minestom.nbt.NBT;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class DecoratedPotPlacement extends BlockPlacementRule {
    public DecoratedPotPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = new LooksFacingXZPlacement(Block.DECORATED_POT).waterlogged().opposite().blockPlace(state);

        CompoundBinaryTag nbt = NBT.compound(c -> {
            c.putCompoundList("minecraft:container", new ArrayList<>());
            c.putStringList("minecraft:pot_decorations", new ArrayList<>());
        });

        return block.withNbt(nbt);
    }
}
