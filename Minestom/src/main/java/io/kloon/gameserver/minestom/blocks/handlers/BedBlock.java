package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.properties.enums.BedPart;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import net.minestom.server.instance.block.Block;

import java.util.Set;

public class BedBlock extends FacingXZBlock {
    public static final EnumProp<BedPart> BED_PART = new EnumProp<>("part", BedPart.class);

    public static final Set<Block> BLOCKS = Sets.newHashSet(
            Block.WHITE_BED,
            Block.GRAY_BED,
            Block.LIGHT_GRAY_BED,
            Block.BLACK_BED,
            Block.BROWN_BED,
            Block.RED_BED,
            Block.ORANGE_BED,
            Block.YELLOW_BED,
            Block.LIME_BED,
            Block.GREEN_BED,
            Block.CYAN_BED,
            Block.LIGHT_BLUE_BED,
            Block.BLUE_BED,
            Block.PURPLE_BED,
            Block.MAGENTA_BED,
            Block.PINK_BED
    );
}
