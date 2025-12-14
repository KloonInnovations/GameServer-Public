package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.ImmutableBiMap;
import io.kloon.gameserver.minestom.blocks.family.ColorFamily;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

public class CakeBlock {
    public static final ImmutableBiMap<Material, Block> CANDLE_TO_CAKE;

    static {
        ImmutableBiMap.Builder<Material, Block> builder = ImmutableBiMap.builder();
        builder.put(Material.CANDLE, Block.CANDLE_CAKE);
        for (ColorFamily colorFamily : ColorFamily.getAll()) {
            Material candle = colorFamily.getMaterial(ColorFamily.Variant.CANDLE);
            Block cake = colorFamily.getBlock(ColorFamily.Variant.CANDLE_CAKE);
            builder.put(candle, cake);
        }
        CANDLE_TO_CAKE = builder.build();
    }

    public static final IntProp BITES = new IntProp("bites", 0, 7);
    public static final BooleanProp LIT = new BooleanProp("lit");
}
