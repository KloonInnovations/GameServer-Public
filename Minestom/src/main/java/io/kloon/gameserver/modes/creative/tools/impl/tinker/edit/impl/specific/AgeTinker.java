package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

import java.util.*;

public class AgeTinker implements TinkerEditHandler {
    public static final Set<Block> AGE_BLOCKS = Sets.newHashSet(
            Block.PUMPKIN_STEM,
            Block.CACTUS,
            Block.FROSTED_ICE,
            Block.WEEPING_VINES,
            Block.POTATOES,
            Block.CHORUS_FLOWER,
            Block.PITCHER_CROP,
            Block.MELON_STEM,
            Block.WHEAT,
            Block.MANGROVE_PROPAGULE,
            Block.CAVE_VINES,
            Block.BEETROOTS,
            Block.CARROTS,
            Block.KELP,
            Block.BAMBOO,
            Block.SUGAR_CANE,
            Block.NETHER_WART,
            Block.COCOA,
            Block.TORCHFLOWER_CROP,
            Block.TWISTING_VINES,
            Block.SWEET_BERRY_BUSH
    );

    private static final HashMap<Block, IntProp> AGE_PROPS = new HashMap<>();

    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        IntProp prop = getAgeProp(block);
        int age = prop.get(block) + 1;
        if (age >= prop.getMaxExcluded()) {
            age = prop.getMinIncluded();
        }

        return prop.get(age).on(block);
    }

    private static IntProp getAgeProp(Block block) {
        return AGE_PROPS.computeIfAbsent(block.defaultState(), AgeTinker::computeAgeProp);
    }

    private static IntProp computeAgeProp(Block block) {
        Collection<String> agesStr = block.propertyOptions().get("age");
        List<Integer> ages = agesStr.stream().map(Integer::parseInt).toList();

        int min = ages.stream().min(Comparator.comparingInt(x -> x)).get();
        int max = ages.stream().max(Comparator.comparingInt(x -> x)).get();

        return new IntProp("age", min, max + 1);
    }
}
