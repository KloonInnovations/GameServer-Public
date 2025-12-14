package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.instance.block.Block;

import java.util.Set;

public class CampfireBlock extends FacingXZBlock {
    public static final BooleanProp LIT = new BooleanProp("lit");
    public static final BooleanProp SIGNAL_FIRE = new BooleanProp("signal_fire");

    public static final Set<Block> BLOCKS = Sets.newHashSet(
            Block.CAMPFIRE,
            Block.SOUL_CAMPFIRE
    );

    public static boolean isSmokeSource(Block block) {
        return block.defaultState() == Block.HAY_BLOCK;
    }
}
