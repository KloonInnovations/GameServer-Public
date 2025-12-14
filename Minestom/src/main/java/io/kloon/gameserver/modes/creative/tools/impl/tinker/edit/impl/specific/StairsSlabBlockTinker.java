package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.handlers.SlabBlock;
import io.kloon.gameserver.minestom.blocks.handlers.StairBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.properties.enums.SlabType;
import io.kloon.gameserver.minestom.blocks.properties.enums.StairHalf;
import io.kloon.gameserver.minestom.blocks.properties.enums.StairShape;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

public class StairsSlabBlockTinker implements TinkerEditHandler {
    private static final Logger LOG = LoggerFactory.getLogger(StairsSlabBlockTinker.class);

    public static final Set<Block> BLOCKS = new HashSet<>();
    static {
        for (BlockFamily family : BlockFamily.getAll()) {
            if (family.block() != null && family.slab() != null && family.stairs() != null) {
                BLOCKS.add(family.block());
                BLOCKS.add(family.slab());
                BLOCKS.add(family.stairs());
            }
        }
    }

    private static final Map<Block, Model> MODEL_BY_BLOCK = new HashMap<>();

    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        Model model = getModel(block);

        Part part = computePart(raycastEntry);
        if (part == null) {
            LOG.warn("Unidentified part!");
            return block;
        }

        model = model.toggled(part);

        Block editedBlock = computeBlock(model);
        if (editedBlock == null || editedBlock.isAir()) {
            return block;
        }

        return editedBlock;
    }

    @Nullable
    private Part computePart(Vec raycastEntry) {
        Vec middle = raycastEntry.sub(0.5);
        Vec signum = middle.apply(SIGNUM_NO_ZERO);
        return PART_BY_SIGNUM.get(signum);
    }

    private Model getModel(Block block) {
        if (true) {
            return computeModel(block);
        }
        return MODEL_BY_BLOCK.computeIfAbsent(block, block1 -> {
            return computeModel(block1);
        });
    }

    @Nullable
    private Model computeModel(Block block) {
        if (block.isAir()) {
            return new Model(BlockFamily.STONE, EnumSet.noneOf(Part.class));
        }

        BlockFamily family = BlockFamily.getFamily(block);
        if (block == family.block()) {
            return new Model(family, EnumSet.allOf(Part.class));
        }

        if (block.defaultState() == family.slab()) {
            SlabType slabType = SlabBlock.TYPE.get(block);
            return switch (slabType) {
                case DOUBLE -> new Model(family, EnumSet.allOf(Part.class));
                case BOTTOM -> new Model(family, EnumSet.copyOf(BOTTOM_PARTS));
                case TOP -> new Model(family, EnumSet.copyOf(TOP_PARTS));
            };
        }

        if (block.defaultState() != family.stairs()) {
            return null;
        }

        StairHalf half = StairBlock.HALF.get(block);
        FacingXZ facing = StairBlock.FACING.get(block);
        StairShape shape = StairBlock.SHAPE.get(block);

        Set<Part> parts = new HashSet<>();
        if (half == StairHalf.BOTTOM) {
            parts.addAll(BOTTOM_PARTS);
        } else {
            parts.addAll(TOP_PARTS);
        }

        List<Part> eastParts = new ArrayList<>();
        switch (shape) {
            case STRAIGHT -> {
                Part left = half == StairHalf.BOTTOM ? Part.TOP_FAR_LEFT : Part.BOTTOM_FAR_LEFT;
                Part right = left.rotateClockwise();
                eastParts.add(left);
                eastParts.add(right);
            }
            case INNER_LEFT -> {
                if (half == StairHalf.BOTTOM) {
                    eastParts.addAll(Arrays.asList(Part.TOP_CLOSE_LEFT, Part.TOP_FAR_LEFT, Part.TOP_FAR_RIGHT));
                } else {
                    eastParts.addAll(Arrays.asList(Part.BOTTOM_CLOSE_LEFT, Part.BOTTOM_FAR_LEFT, Part.BOTTOM_FAR_RIGHT));
                }
            }
            case INNER_RIGHT -> {
                if (half == StairHalf.BOTTOM) {
                    eastParts.addAll(Arrays.asList(Part.TOP_FAR_LEFT, Part.TOP_FAR_RIGHT, Part.TOP_CLOSE_RIGHT));
                } else {
                    eastParts.addAll(Arrays.asList(Part.BOTTOM_FAR_LEFT, Part.BOTTOM_FAR_RIGHT, Part.BOTTOM_CLOSE_RIGHT));
                }
            }
            case OUTER_LEFT -> eastParts.add(half == StairHalf.BOTTOM ? Part.TOP_FAR_LEFT : Part.BOTTOM_FAR_LEFT);
            case OUTER_RIGHT -> eastParts.add(half == StairHalf.BOTTOM ? Part.TOP_FAR_RIGHT : Part.BOTTOM_FAR_RIGHT);
        }
        for (Part eastPart : eastParts) {
            Part rotated = eastPart.rotateClockwise(facing);
            parts.add(rotated);
        }
        return new Model(family, EnumSet.copyOf(parts));
    }

    @Nullable
    private Block computeBlock(Model model) {
        BlockFamily family = model.family();
        EnumSet<Part> parts = model.parts();

        Set<Part> tops = new HashSet<>(Sets.intersection(parts, TOP_PARTS));
        Set<Part> bottoms = new HashSet<>(Sets.intersection(parts, BOTTOM_PARTS));
        int topsSz = tops.size();
        int bottomsSz = bottoms.size();

        if (topsSz + bottomsSz == 0) {
            return Block.AIR;
        } if (topsSz + bottomsSz == 8) {
            return family.block();
        }

        if (topsSz == 4) {
            if (bottomsSz == 0) {
                return SlabBlock.TYPE.get(SlabType.TOP).on(family.slab());
            }
            if (bottomsSz == 1) {
                Part soloPart = bottoms.iterator().next();
                return soloPart.soloShape.apply(family);
            } if (bottomsSz == 3) {
                Part holePart = Sets.difference(BOTTOM_PARTS, bottoms).iterator().next();
                return holePart.holeShape.apply(family);
            }

            Iterator<Part> it = bottoms.iterator();
            Part a = it.next();
            Part b = it.next();
            FacingXZ facing = findAdjacence(a, b);
            if (facing == null) {
                return null;
            }

            Block block = family.stairs();
            block = StairBlock.HALF.get(StairHalf.TOP).on(block);
            return StairBlock.FACING.get(facing).on(block);
        }

        if (bottomsSz == 4) {
            if (topsSz == 0) {
                return SlabBlock.TYPE.get(SlabType.BOTTOM).on(family.slab());
            }
            if (topsSz == 1) {
                Part soloPart = tops.iterator().next();
                return soloPart.soloShape.apply(family);
            } if (topsSz == 3) {
                Part holePart = Sets.difference(TOP_PARTS, tops).iterator().next();
                return holePart.holeShape.apply(family);
            }

            Iterator<Part> it = tops.iterator();
            Part a = it.next();
            Part b = it.next();
            FacingXZ facing = findAdjacence(a, b);
            if (facing == null) {
                return null;
            }

            Block block = family.stairs();
            block = StairBlock.HALF.get(StairHalf.BOTTOM).on(block);
            return StairBlock.FACING.get(facing).on(block);
        }

        return null;
    }

    @Nullable
    private FacingXZ findAdjacence(Part a, Part b) {
        if (a.sigY != b.sigY) return null;
        if (a.sigX == b.sigX) {
            return a.sigX == 1 ? FacingXZ.EAST : FacingXZ.WEST;
        }
        if (a.sigZ == b.sigZ) {
            return a.sigZ == 1 ? FacingXZ.SOUTH : FacingXZ.NORTH;
        }
        return null;
    }

    private record Model(BlockFamily family, EnumSet<Part> parts) {
        public Model toggled(Part part) {
            EnumSet<Part> copy = EnumSet.copyOf(parts);
            if (!copy.add(part)) {
                copy.remove(part);
            }
            return new Model(family, copy);
        }
    }

    private static final Set<Part> TOP_PARTS = Set.of(Part.TOP_CLOSE_LEFT, Part.TOP_CLOSE_RIGHT, Part.TOP_FAR_LEFT, Part.TOP_FAR_RIGHT);
    private static final Set<Part> BOTTOM_PARTS = Set.of(Part.BOTTOM_CLOSE_LEFT, Part.BOTTOM_CLOSE_RIGHT, Part.BOTTOM_FAR_LEFT, Part.BOTTOM_FAR_RIGHT);
    private static final Map<Vec, Part> PART_BY_SIGNUM = Maps.uniqueIndex(Arrays.asList(Part.values()), p -> new Vec(p.sigX, p.sigY, p.sigZ));
    private enum Part {
        BOTTOM_CLOSE_LEFT(-1, -1, -1, stairs(true, true, StairShape.OUTER_RIGHT), stairs(true, false, StairShape.INNER_RIGHT)),
        BOTTOM_CLOSE_RIGHT(-1, -1, 1, stairs(true, true, StairShape.OUTER_LEFT), stairs(true, false, StairShape.INNER_LEFT)),
        BOTTOM_FAR_LEFT(1, -1, -1, stairs(true, false, StairShape.OUTER_LEFT), stairs(true, true, StairShape.INNER_LEFT)),
        BOTTOM_FAR_RIGHT(1, -1, 1, stairs(true, false, StairShape.OUTER_RIGHT), stairs(true, true, StairShape.INNER_RIGHT)),
        TOP_CLOSE_LEFT(-1, 1, -1, stairs(false, true, StairShape.OUTER_RIGHT), stairs(false, false, StairShape.INNER_RIGHT)),
        TOP_CLOSE_RIGHT(-1, 1, 1, stairs(false, true, StairShape.OUTER_LEFT), stairs(false, false, StairShape.INNER_LEFT)),
        TOP_FAR_LEFT(1, 1, -1, stairs(false, false, StairShape.OUTER_LEFT), stairs(false, true, StairShape.INNER_LEFT)),
        TOP_FAR_RIGHT(1, 1, 1, stairs(false, false, StairShape.OUTER_RIGHT), stairs(false, true, StairShape.INNER_RIGHT)),
        ;

        private final int sigX;
        private final int sigY;
        private final int sigZ;
        private final Function<BlockFamily, Block> soloShape;
        private final Function<BlockFamily, Block> holeShape;

        Part(int sigX, int sigY, int sigZ, Function<BlockFamily, Block> soloShape, Function<BlockFamily, Block> holeShape) {
            this.sigX = sigX;
            this.sigY = sigY;
            this.sigZ = sigZ;
            this.soloShape = soloShape;
            this.holeShape = holeShape;
        }

        public Part rotateClockwise() {
            return switch (this) {
                case BOTTOM_CLOSE_LEFT -> BOTTOM_FAR_LEFT;
                case BOTTOM_FAR_LEFT -> BOTTOM_FAR_RIGHT;
                case BOTTOM_FAR_RIGHT -> BOTTOM_CLOSE_RIGHT;
                case BOTTOM_CLOSE_RIGHT -> BOTTOM_CLOSE_LEFT;

                case TOP_CLOSE_LEFT -> TOP_FAR_LEFT;
                case TOP_FAR_LEFT -> TOP_FAR_RIGHT;
                case TOP_FAR_RIGHT -> TOP_CLOSE_RIGHT;
                case TOP_CLOSE_RIGHT -> TOP_CLOSE_LEFT;
            };
        }

        public Part rotateClockwise(FacingXZ facing) {
            int rotations = switch (facing) {
                case EAST -> 0;
                case SOUTH -> 1;
                case WEST -> 2;
                case NORTH -> 3;
            };
            Part part = this;
            for (int i = 0; i < rotations; ++i) {
                part = part.rotateClockwise();
            }
            return part;
        }
    }

    private static Function<BlockFamily, Block> stairs(boolean topFull, boolean flip, StairShape shape) {
        return family -> {
            Block block = family.stairs();
            block = StairBlock.HALF.get(topFull ? StairHalf.TOP : StairHalf.BOTTOM).on(block);
            block = StairBlock.FACING.get(flip ? FacingXZ.WEST : FacingXZ.EAST).on(block);
            block = StairBlock.SHAPE.get(shape).on(block);
            return block;
        };
    }

    public static final Vec.Operator SIGNUM_NO_ZERO = new Vec.Operator() {
        @Override
        public @NotNull Vec apply(double x, double y, double z) {
            return new Vec(
                    x == 0 ? 1 : Math.signum(x),
                    y == 0 ? 1 : Math.signum(y),
                    z == 0 ? 1 : Math.signum(z)
            );
        }
    };
}
