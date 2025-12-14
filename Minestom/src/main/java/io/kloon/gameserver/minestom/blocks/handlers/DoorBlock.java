package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import net.minestom.server.instance.block.Block;

import java.util.Set;

public class DoorBlock {
    public static final EnumProp<FacingXZ> FACING = BlockProp.FACING_XZ;
    public static final EnumProp<Half> HALF = new EnumProp<>("half", Half.class);
    public static final EnumProp<Hinge> HINGE = new EnumProp<>("hinge", Hinge.class);
    public static final BooleanProp OPEN = new BooleanProp("open");
    public static final BooleanProp POWERED = new BooleanProp("powered");

    public static final Set<Block> PLAYER_DOORS = Set.of(
            Block.OAK_DOOR,
            Block.SPRUCE_DOOR,
            Block.BIRCH_DOOR,
            Block.JUNGLE_DOOR,
            Block.ACACIA_DOOR,
            Block.DARK_OAK_DOOR,
            Block.MANGROVE_DOOR,
            Block.CHERRY_DOOR,
            Block.BAMBOO_DOOR,
            Block.CRIMSON_DOOR,
            Block.WARPED_DOOR,
            Block.COPPER_DOOR,
            Block.WAXED_COPPER_DOOR,
            Block.EXPOSED_COPPER_DOOR,
            Block.WAXED_EXPOSED_COPPER_DOOR,
            Block.WEATHERED_COPPER_DOOR,
            Block.WAXED_WEATHERED_COPPER_DOOR,
            Block.OXIDIZED_COPPER_DOOR,
            Block.WAXED_OXIDIZED_COPPER_DOOR
    );

    public enum Half {
        LOWER,
        UPPER,
        ;

        public Half opposite() {
            return this == LOWER ? UPPER : LOWER;
        }
    }

    public enum Hinge {
        LEFT,
        RIGHT,
        ;

        public Hinge opposite() {
            return this == LEFT ? RIGHT : LEFT;
        }
    }
}
