package io.kloon.gameserver.minestom.blocks.properties;

import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXYZ;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;

public record BlockProp(
        String key,
        String value
) {
    public Block on(Block block) {
        return block.withProperty(key(), value());
    }

    public Block on(BlockPlacementRule.PlacementState state) {
        return on(state.block());
    }

    public static final EnumProp<FacingXYZ> FACING_XYZ = new EnumProp<>("facing", FacingXYZ.class);
    public static final EnumProp<FacingXZ> FACING_XZ = new EnumProp<>("facing", FacingXZ.class);
}
