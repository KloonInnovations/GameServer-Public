package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.ImmutableBiMap;
import io.kloon.gameserver.minestom.blocks.properties.BannerRotProp;
import io.kloon.gameserver.minestom.blocks.transforms.BlockTransformer;
import io.kloon.gameserver.minestom.blocks.transforms.FlipTransform;
import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import net.minestom.server.instance.block.Block;

public class BannerBlock {
    public static final BannerRotProp ROTATION = new BannerRotProp("rotation");
    private static final int SEGMENTS = 16;

    public static final ImmutableBiMap<Block, Block> BLOCK_TO_WALL = ImmutableBiMap.<Block, Block>builder()
            .put(Block.WHITE_BANNER, Block.WHITE_WALL_BANNER)
            .put(Block.LIGHT_GRAY_BANNER, Block.LIGHT_GRAY_WALL_BANNER)
            .put(Block.GRAY_BANNER, Block.GRAY_WALL_BANNER)
            .put(Block.BLACK_BANNER, Block.BLACK_WALL_BANNER)
            .put(Block.BROWN_BANNER, Block.BROWN_WALL_BANNER)
            .put(Block.RED_BANNER, Block.RED_WALL_BANNER)
            .put(Block.ORANGE_BANNER, Block.ORANGE_WALL_BANNER)
            .put(Block.YELLOW_BANNER, Block.YELLOW_WALL_BANNER)
            .put(Block.LIME_BANNER, Block.LIME_WALL_BANNER)
            .put(Block.GREEN_BANNER, Block.GREEN_WALL_BANNER)
            .put(Block.CYAN_BANNER, Block.CYAN_WALL_BANNER)
            .put(Block.LIGHT_BLUE_BANNER, Block.LIGHT_BLUE_WALL_BANNER)
            .put(Block.BLUE_BANNER, Block.BLUE_WALL_BANNER)
            .put(Block.PURPLE_BANNER, Block.PURPLE_WALL_BANNER)
            .put(Block.MAGENTA_BANNER, Block.MAGENTA_WALL_BANNER)
            .put(Block.PINK_BANNER, Block.PINK_WALL_BANNER)
            .build();

    public static class StandingBannerTransformer implements BlockTransformer {
        @Override
        public Block rotate(Block block, RotationTransform rotation) {
            int segment = ROTATION.getSegment(block);
            int rotated = switch (rotation) {
                case NONE -> segment;
                case CLOCKWISE_90 -> (segment + SEGMENTS / 4) % 16;
                case CLOCKWISE_180 -> (segment + SEGMENTS / 2) % 16;
                case CLOCKWISE_270 -> (segment + SEGMENTS * 3 / 4) % 16;
            };

            return ROTATION.get(rotated).on(block);
        }

        @Override
        public Block flip(Block block, FlipTransform flip) {
            int segment = ROTATION.getSegment(block);

            int half = SEGMENTS / 2;
            int invert = segment - SEGMENTS;
            
            int rotated = switch (flip) {
                case NONE -> segment;
                case LEFT_RIGHT -> (half - invert + SEGMENTS) % segment;
                case FRONT_BACK -> (SEGMENTS - invert) % segment;
            };
            return ROTATION.get(rotated).on(block);
        }
    }
}
