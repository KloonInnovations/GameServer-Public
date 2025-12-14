package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit;

import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.family.InteractibleFamily;
import io.kloon.gameserver.minestom.blocks.family.WoodFamily;
import io.kloon.gameserver.minestom.blocks.handlers.*;
import io.kloon.gameserver.minestom.blocks.handlers.signs.CeilingHangingSignBlock;
import io.kloon.gameserver.minestom.blocks.properties.PropertyLookups;
import io.kloon.gameserver.minestom.blocks.properties.enums.BigDripleafTilt;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.generic.*;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific.AgeTinker;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific.*;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TinkerEditHandlers {
    private final Map<Block, TinkerEditHandler> handlers = new HashMap<>();

    public TinkerEditHandlers() {
        for (Block block : PropertyLookups.LIT_BLOCKS) {
            handlers.put(block, new ToggleBooleanTinker(PropertyLookups.LIT));
        }
        for (Block block : PropertyLookups.POWERED_BLOCKS) {
            handlers.put(block, new ToggleBooleanTinker(PropertyLookups.POWERED));
        }
        for (Block block : StairsSlabBlockTinker.BLOCKS) {
            handlers.put(block, new StairsSlabBlockTinker());
        }
        for (Block block : WallBlock.BLOCKS) {
            handlers.put(block, new WallTinker());
        }
        for (Block block : FenceBlock.BLOCKS) {
            handlers.put(block, new FenceTinker());
        }
        for (Block block : AgeTinker.AGE_BLOCKS) {
            handlers.put(block, new AgeTinker());
        }
        for (Block block : AmethystClusterBlock.BLOCK_LIST) {
            handlers.put(block, new CycleBlocksTinker(AmethystClusterBlock.BLOCK_LIST));
        }
        for (Block block : BannerBlock.BLOCK_TO_WALL.keySet()) {
            handlers.put(block, new BannerTinker());
        }
        for (Block block : CakeBlock.CANDLE_TO_CAKE.values()) {
            handlers.put(block, new CandleCakeTinker());
        }
        for (Block block : PumpkinTinker.BLOCKS) {
            handlers.put(block, new PumpkinTinker());
        }
        for (Block block : InteractibleFamily.getBlocksWithFeature(InteractibleFamily.Feature.DOOR)) {
            handlers.put(block, new ToggleBooleanTinker(DoorBlock.OPEN));
        }
        for (Block block : InteractibleFamily.getBlocksWithFeature(InteractibleFamily.Feature.TRAPDOOR)) {
            handlers.put(block, new ToggleBooleanTinker(TrapdoorBlock.OPEN));
        }
        for (Block block : FenceGateBlock.BLOCKS) {
            handlers.put(block, new ToggleBooleanTinker(FenceGateBlock.IN_WALL));
        }
        for (Block block : FurnaceBlock.BLOCKS) {
            handlers.put(block, new ToggleBooleanTinker(FurnaceBlock.LIT));
        }
        for (Block block : PaneBlock.BLOCKS) {
            handlers.put(block, new PaneTinker());
        }
        for (Block block : LanternBlock.BLOCKS) {
            handlers.put(block, new ToggleBooleanTinker(LanternBlock.HANGING));
        }
        for (Block block : BlockFamily.Variant.HANGING_SIGN.getBlocks()) {
            handlers.put(block, new ToggleBooleanTinker(CeilingHangingSignBlock.ATTACHED));
        }
        handlers.put(Block.ATTACHED_PUMPKIN_STEM, new CycleEnumTinker<>(FacingXZBlock.FACING_XZ));
        handlers.put(Block.ATTACHED_MELON_STEM, new CycleEnumTinker<>(FacingXZBlock.FACING_XZ));
        handlers.put(Block.BAMBOO, new BambooStalkTinker());
        handlers.put(Block.BEEHIVE, new HoneyLevelTinker());
        handlers.put(Block.BEE_NEST, new HoneyLevelTinker());
        handlers.put(Block.BREWING_STAND, new BrewingStandTinker());
        handlers.put(Block.CAKE, new CycleIntTinker(CakeBlock.BITES));
        handlers.put(Block.WATER_CAULDRON, new CycleIntTinker(CauldronBlock.LEVEL));
        handlers.put(Block.POWDER_SNOW_CAULDRON, new CycleIntTinker(CauldronBlock.LEVEL));
        handlers.put(Block.CAVE_VINES, new ToggleBooleanTinker(CaveVineBlock.BERRIES));
        handlers.put(Block.CAVE_VINES_PLANT, new ToggleBooleanTinker(CaveVineBlock.BERRIES));
        handlers.put(Block.CHISELED_BOOKSHELF, new ChiseledBookshelfTinker());
        handlers.put(Block.CHORUS_PLANT, new ChorusPlantTinker());
        handlers.put(Block.COMPOSTER, new CycleIntTinker(ComposterBlock.LEVEL));
        handlers.put(Block.END_PORTAL_FRAME, new ToggleBooleanTinker(EndPortalFrameBlock.EYE));
        handlers.put(Block.FARMLAND, new ToggleIntMaxTinker(FarmlandBlock.MOISTURE));
        handlers.put(Block.LECTERN, new ToggleBooleanTinker(LecternBlock.HAS_BOOK));
        handlers.put(Block.PLAYER_HEAD, new CycleIntTinker(SkullBlock.GROUND_ROTATION));
        handlers.put(Block.SCAFFOLDING, new ToggleBooleanTinker(ScaffoldingBlock.BOTTOM));
        handlers.put(Block.BIG_DRIPLEAF, new CycleEnumPartialTinker<>(BigDripleafBlock.TILT, BigDripleafTilt.NONE, BigDripleafTilt.PARTIAL, BigDripleafTilt.FULL));
        handlers.put(Block.SNOW_BLOCK, new SnowTinker());
        handlers.put(Block.SNOW, new SnowTinker());
        handlers.put(Block.TURTLE_EGG, new CycleIntTinker(TurtleEggBlock.EGGS));
        register(new CycleBlocksTinker(Block.AZALEA_LEAVES, Block.FLOWERING_AZALEA_LEAVES));
        register(new CycleBlocksTinker(Block.AZALEA, Block.FLOWERING_AZALEA));
        register(new CycleBlocksTinker(Block.BAMBOO_BLOCK, Block.STRIPPED_BAMBOO_BLOCK));
        register(new CycleBlocksTinker(Block.SAND, Block.SUSPICIOUS_SAND));
        register(new CycleBlocksTinker(Block.GRAVEL, Block.SUSPICIOUS_GRAVEL));

        registerStrippableWood();
    }

    private void register(CycleBlocksTinker cycleEdit) {
        for (Block block : cycleEdit.getBlocks()) {
            handlers.put(block, cycleEdit);
        }
    }

    private void registerStrippableWood() {
        for (WoodFamily family : WoodFamily.getAll()) {
            Block log = family.log();
            Block strippedLog = family.strippedLog();
            if (log != null && strippedLog != null) {
                register(new CycleBlocksTinker(log, strippedLog));
            }

            Block wood = family.wood();
            Block strippedWood = family.strippedWood();
            if (wood != null && strippedWood != null) {
                register(new CycleBlocksTinker(wood, strippedWood));
            }
        }
    }

    @Nullable
    public TinkerEditHandler get(Block block) {
        return handlers.get(block.defaultState());
    }
}
