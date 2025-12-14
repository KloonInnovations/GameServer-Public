package io.kloon.gameserver.minestom.blocks;

import io.kloon.gameserver.minestom.blocks.family.InteractibleFamily;
import io.kloon.gameserver.minestom.blocks.transforms.impl.AxisTransformer;
import io.kloon.gameserver.minestom.blocks.transforms.impl.CrafterTransformer;
import io.kloon.gameserver.minestom.blocks.transforms.impl.FacingTransformer;
import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.family.CoralFamily;
import io.kloon.gameserver.minestom.blocks.family.WoodFamily;
import io.kloon.gameserver.minestom.blocks.handlers.*;
import io.kloon.gameserver.minestom.blocks.placement.generic.*;
import io.kloon.gameserver.minestom.blocks.placement.impl.*;
import io.kloon.gameserver.minestom.blocks.placement.impl.sign.HangingSignPlacement;
import io.kloon.gameserver.minestom.blocks.placement.impl.sign.SignPlacement;
import io.kloon.gameserver.minestom.blocks.transforms.BlockTransformer;
import io.kloon.gameserver.minestom.blocks.transforms.impl.HopperTransformer;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaBlockInteraction;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaInteractionListener;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.impl.*;
import io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.*;
import io.kloon.gameserver.minestom.blocks.vanilla.tiles.VanillaTiles;
import io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.campfire.CampfireHandler;
import io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.decoratedpot.DecoratedPotHandler;
import io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.sign.SignHandler;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class KloonPlacementRules {
    private static final Map<Integer, BlockTransformer> TRANSFORMERS = new HashMap<>();
    private static final VanillaInteractionListener interactionListener = new VanillaInteractionListener();
    private static final VanillaTiles vanillaTiles = new VanillaTiles();

    private static void registerPlacement(BlockPlacementRule rule) {
        MinecraftServer.getBlockManager().registerBlockPlacementRule(rule);
    }

    private static void registerTransform(Block block, BlockTransformer transformer) {
        TRANSFORMERS.put(block.id(), transformer);
    }

    private static void registerVanilla(Block block, VanillaBlockInteraction vanilla) {
        interactionListener.register(block, vanilla);
    }

    private static void registerTile(Block block, Key tileId, Supplier<BlockHandler> handler) {
        vanillaTiles.register(block, tileId, handler);
    }

    private static void register(Block block, BlockPlacementRule rule, BlockTransformer transformer) {
        registerPlacement(rule);
        registerTransform(block, transformer);
    }

    private static void register(Block block, Function<Block, BlockPlacementRule> ruleFunc, BlockTransformer transformer) {
        BlockPlacementRule rule = ruleFunc.apply(block);
        registerPlacement(rule);
        registerTransform(block, transformer);
    }

    public static void register() {
        ButtonBlock.BLOCKS.forEach(button -> {
            registerPlacement(new AttachedFacingXZBlock.Placement(button));
            registerTransform(button, new FacingTransformer());
        });

        FurnaceBlock.BLOCKS.forEach(furnace -> {
            registerPlacement(new LooksFacingXZPlacement(furnace));
            registerTransform(furnace, new FacingTransformer());
        });

        AmethystClusterBlock.BLOCKS.forEach(amethyst -> {
            registerPlacement(new AmethystClusterPlacement(amethyst));
            registerTransform(amethyst, new FacingTransformer());
        });

        AnvilBlock.BLOCKS.forEach(anvil -> {
            registerPlacement(new AnvilPlacement(anvil));
            registerTransform(anvil, new FacingTransformer());
        });

        BannerBlock.BLOCK_TO_WALL.keySet().forEach(standingBanner -> {
            registerPlacement(new BannerPlacement(standingBanner));
            registerTransform(standingBanner, new BannerBlock.StandingBannerTransformer());
            registerTile(standingBanner, BannerHandler.ID, BannerHandler::new);
        });
        BannerBlock.BLOCK_TO_WALL.values().forEach(wallBanner -> {
            registerTransform(wallBanner, new FacingTransformer());
        });

        Stream.of(
                Block.BARREL,
                Block.DISPENSER,
                Block.DROPPER,
                Block.PISTON,
                Block.STICKY_PISTON
        ).forEach(block -> {
            registerPlacement(new LooksFacingXYZPlacement(block));
            registerTransform(block, new FacingTransformer());
        });
        registerPlacement(new LooksFacingXYZPlacement(Block.OBSERVER).opposite());
        registerTransform(Block.OBSERVER, new FacingTransformer());

        Stream.of(
                Block.LIGHTNING_ROD
        ).forEach(block -> {
            registerPlacement(new ClickFacingXYZPlacement(block).waterlogged());
            registerTransform(block, new FacingTransformer());
        });
        registerPlacement(new ClickFacingXYZPlacement(Block.END_ROD));
        registerTransform(Block.END_ROD, new FacingTransformer());

        Stream.of(
                Block.CARVED_PUMPKIN,
                Block.JACK_O_LANTERN,
                Block.CHISELED_BOOKSHELF,
                Block.LOOM,
                Block.REPEATER,
                Block.COMPARATOR,
                Block.END_PORTAL_FRAME,
                Block.STONECUTTER,
                Block.LECTERN
        ).forEach(horizontalDirectionBlock -> {
            registerPlacement(new LooksFacingXZPlacement(horizontalDirectionBlock));
            registerTransform(horizontalDirectionBlock, new FacingTransformer());
        });
        Stream.of(
                Block.ENDER_CHEST,
                Block.BIG_DRIPLEAF,
                Block.BIG_DRIPLEAF_STEM,
                Block.CALIBRATED_SCULK_SENSOR
        ).forEach(horizontalDirectionBlockWaterlogged -> {
            registerPlacement(new LooksFacingXZPlacement(horizontalDirectionBlockWaterlogged).waterlogged());
            registerTransform(horizontalDirectionBlockWaterlogged, new FacingTransformer());
        });
        register(Block.DECORATED_POT, new DecoratedPotPlacement(Block.DECORATED_POT), new FacingTransformer());

        Stream.of(
                Block.INFESTED_DEEPSLATE,
                Block.HAY_BLOCK
        ).forEach(pillarBlock -> {
            register(pillarBlock, new PillarPlacement(pillarBlock), new AxisTransformer());
        });
        register(Block.CHAIN, new PillarPlacement(Block.CHAIN).waterlogged(), new AxisTransformer());

        Stream.of(
                Block.SCULK_SHRIEKER,
                Block.SCULK_SENSOR
        ).forEach(waterlogged -> {
            registerPlacement(new WaterloggedPlacement(waterlogged));
        });

        Stream.of(
                Block.GRINDSTONE,
                Block.LEVER
        ).forEach(attachedXzBlock -> {
            register(attachedXzBlock, AttachedFacingXZBlock.Placement::new, new FacingTransformer());
        });

        registerPlacement(new ChorusPlantPlacement(Block.CHORUS_PLANT));
        register(Block.COCOA, CocoaPlacement::new, new FacingTransformer());
        register(Block.CRAFTER, CrafterPlacement::new, new CrafterTransformer());
        register(Block.HOPPER, HopperPlacement::new, new HopperTransformer());
        register(Block.LADDER, b -> new ClickFacingXZPlacement(b).waterlogged(), new FacingTransformer());
        register(Block.TRIPWIRE_HOOK, ClickFacingXZPlacement::new, new FacingTransformer());
        register(Block.PINK_PETALS, PinkPetalPlacement::new, new FacingTransformer());
        registerPlacement(new DripstonePlacement(Block.POINTED_DRIPSTONE));
        registerPlacement(new FourCounterPlacement(Block.SEA_PICKLE, SeaPickleBlock.PICKLES).waterlogged());
        registerPlacement(new FourCounterPlacement(Block.TURTLE_EGG, TurtleEggBlock.EGGS));
        registerPlacement(new SnowLayerPlacement(Block.SNOW));
        registerPlacement(new VinePlacement(Block.VINE));
        registerPlacement(new RedstoneWirePlacement(Block.REDSTONE_WIRE));
        registerPlacement(new StringPlacement(Block.TRIPWIRE));

        registerVanilla(Block.CAKE, new CakeVanillaInteraction());
        registerVanilla(Block.PUMPKIN, new PumpkinVanillaInteraction());
        registerVanilla(Block.END_PORTAL_FRAME, new EndPortalFrameVanillaInteraction());
        registerVanilla(Block.DIRT, new DirtVanillaInteraction());
        registerVanilla(Block.LEVER, new LeverVanillaInteraction());

        registerPlacement(new BellPlacement(Block.BELL));
        registerTile(Block.BELL, BellHandler.ID, BellHandler::new);

        registerTile(Block.ENCHANTING_TABLE, EnchantingTableHandler.ID, EnchantingTableHandler::new);
        registerTile(Block.DECORATED_POT, DecoratedPotHandler.ID, DecoratedPotHandler::new);
        registerTile(Block.END_GATEWAY, EndGatewayHandler.ID, EndGatewayHandler::new);
        registerTile(Block.END_PORTAL, EndPortalHandler.ID, EndPortalHandler::new);
        registerTile(Block.ENDER_CHEST, EnderChestHandler.ID, EnderChestHandler::new);
        registerTile(Block.LECTERN, LecternHandler.ID, LecternHandler::new);

        CauldronBlock.BLOCKS.forEach(cauldron -> {
            registerVanilla(cauldron, new CauldronVanillaInteraction());
        });

        CakeBlock.CANDLE_TO_CAKE.values().forEach(candleCake -> {
            registerVanilla(candleCake, new CakeVanillaInteraction());
        });

        BedBlock.BLOCKS.forEach(bed -> {
            registerPlacement(new BedPlacement(bed));
            registerTransform(bed, new FacingTransformer());
            registerTile(bed, BedHandler.ID, BedHandler::new);
        });

        RailBlock.BLOCKS.forEach(rail -> {
            registerPlacement(new RailPlacement(rail));
        });

        BeehiveBlock.BLOCKS.forEach(beehive -> {
            registerPlacement(new LooksFacingXZPlacement(beehive));
            registerTransform(beehive, new FacingTransformer());
        });

        CampfireBlock.BLOCKS.forEach(campfire -> {
            registerPlacement(new CampfirePlacement(campfire));
            registerTransform(campfire, new FacingTransformer());
            registerTile(campfire, CampfireHandler.ID, CampfireHandler::new);
        });

        CandleBlock.BLOCKS.forEach(candle -> {
            registerPlacement(new CandlePlacement(candle));
        });

        ChestBlock.BLOCKS.forEach(chest -> {
            registerPlacement(new ChestPlacement(chest));
            registerTransform(chest, new FacingTransformer());
        });

        GlazedTerracottaBlock.BLOCKS.forEach(glazedTerracotta -> {
            registerPlacement(new LooksFacingXZPlacement(glazedTerracotta));
            registerTransform(glazedTerracotta, new FacingTransformer());
        });

        StairBlock.BLOCKS.forEach(stair -> {
            registerPlacement(new StairPlacement(stair));
            registerTransform(stair, new FacingTransformer());
        });

        WallBlock.BLOCKS.forEach(wall -> {
            registerPlacement(new WallPlacement(wall));
        });

        FenceGateBlock.BLOCKS.forEach(fenceGate -> {
            registerPlacement(new FenceGatePlacement(fenceGate));
            registerVanilla(fenceGate, new FenceGateVanillaInteraction());
        });

        FenceBlock.BLOCKS.forEach(fence -> {
            registerPlacement(new FencePlacement(fence));
        });

        HugeMushroomBlock.BLOCKS.forEach(hugeMushroom -> {
            registerPlacement(new HugeMushroomPlacement(hugeMushroom));
        });

        PaneBlock.BLOCKS.forEach(pane -> {
            registerPlacement(new PanePlacement(pane));
        });

        LanternBlock.BLOCKS.forEach(lantern -> {
            registerPlacement(new LanternPlacement(lantern));
        });

        TorchBlock.BLOCKS.forEach(torch -> {
            registerPlacement(new TorchPlacement(torch));
            registerTransform(torch, new FacingTransformer());
        });

        ShulkerBoxBlock.BLOCKS.forEach(shulkerBox -> {
            registerPlacement(new ClickFacingXYZPlacement(shulkerBox));
        });

        BlockFamily.getBlocksOfVariant(BlockFamily.Variant.SLAB).forEach(slab -> {
            registerPlacement(new SlabPlacement(slab));
        });

        BlockFamily.getBlocksOfVariant(BlockFamily.Variant.TRAPDOOR).forEach(trapdoor -> {
            registerPlacement(new TrapdoorPlacement(trapdoor));
            registerTransform(trapdoor, new FacingTransformer());
        });

        BlockFamily.getBlocksOfVariant(BlockFamily.Variant.DOOR).forEach(door -> {
            registerPlacement(new DoorPlacement(door));
        });
        DoorBlock.PLAYER_DOORS.forEach(door -> {
            registerVanilla(door, new DoorVanillaInteraction());
        });
        InteractibleFamily.getWithFeature(InteractibleFamily.Feature.TRAPDOOR).forEach(family -> {
            if (!family.canOpenByHand()) return;
            registerVanilla(family.getBlock(InteractibleFamily.Feature.TRAPDOOR), new TrapdoorVanillaInteraction());
        });

        BlockFamily.getBlocksOfVariant(BlockFamily.Variant.SIGN).forEach(sign -> {
            registerPlacement(new SignPlacement(sign));
            registerTile(sign, SignHandler.ID, SignHandler::new);
        });

        BlockFamily.getBlocksOfVariant(BlockFamily.Variant.HANGING_SIGN).forEach(sign -> {
            registerPlacement(new HangingSignPlacement(sign));
            registerTile(sign, SignHandler.ID, SignHandler::new);
        });

        BlockFamily.getBlocksOfVariant(BlockFamily.Variant.WALL_SIGN).forEach(sign -> {
            registerTile(sign, SignHandler.ID, SignHandler::new);
        });

        BlockFamily.getBlocksOfVariant(BlockFamily.Variant.WALL_HANGING_SIGN).forEach(sign -> {
            registerTile(sign, SignHandler.ID, SignHandler::new);
        });

        SkullBlock.BLOCKS.forEach(skull -> {
            registerPlacement(new SkullPlacement(skull));
            registerTile(skull, SkullHandler.ID, SkullHandler::new);
        });
        SkullBlock.BLOCK_TO_WALL.values().forEach(wallSkull -> {
            registerTile(wallSkull, SkullHandler.ID, SkullHandler::new);
        });

        MultifaceBlock.BLOCKS.forEach(multiface -> {
            registerPlacement(new MultifacePlacement(multiface));
        });

        Stream.of(
                CoralFamily.Variant.CORAL,
                CoralFamily.Variant.DEAD_CORAL
        ).forEach(corialVariant -> {
            CoralFamily.getBlocksOfVariant(corialVariant).forEach(coral -> {
                registerPlacement(new WaterloggedPlacement(coral));
            });
        });
        Stream.of(
                CoralFamily.Variant.FAN,
                CoralFamily.Variant.DEAD_FAN
        ).forEach(corialVariant -> {
            CoralFamily.getBlocksOfVariant(corialVariant).forEach(coral -> {
                registerPlacement(new CoralFanPlacement(coral));
            });
        });

        Stream.of(
                WoodFamily.Variant.LOG,
                WoodFamily.Variant.STRIPPED_LOG,
                WoodFamily.Variant.WOOD,
                WoodFamily.Variant.STRIPPED_WOOD
        ).forEach(woodVariant -> {
            WoodFamily.getBlocksOfVariants(woodVariant).forEach(wood -> {
                registerPlacement(new PillarPlacement(wood));
                registerTransform(wood, new AxisTransformer());
            });
        });
        Stream.of(
                Block.STRIPPED_MANGROVE_WOOD,
                Block.BAMBOO_BLOCK,
                Block.STRIPPED_BAMBOO_BLOCK,
                Block.WARPED_STEM,
                Block.STRIPPED_WARPED_STEM,
                Block.CRIMSON_STEM,
                Block.STRIPPED_CRIMSON_STEM,
                Block.MUDDY_MANGROVE_ROOTS,
                Block.BASALT,
                Block.POLISHED_BASALT,
                Block.QUARTZ_PILLAR,
                Block.PURPUR_PILLAR,
                Block.BONE_BLOCK,
                Block.WARPED_HYPHAE,
                Block.STRIPPED_WARPED_HYPHAE,
                Block.CRIMSON_HYPHAE,
                Block.STRIPPED_CRIMSON_HYPHAE,
                Block.DEEPSLATE,
                Block.OCHRE_FROGLIGHT,
                Block.VERDANT_FROGLIGHT,
                Block.PEARLESCENT_FROGLIGHT
        ).forEach(extraWoodPillar -> {
            registerPlacement(new PillarPlacement(extraWoodPillar));
            registerTransform(extraWoodPillar, new AxisTransformer());
        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockInteractEvent.class, interactionListener::handleEvent);
    }

    @Nullable
    public static BlockPlacementRule getPlacementRule(Block block) {
        return MinecraftServer.getBlockManager().getBlockPlacementRule(block);
    }

    @Nullable
    public static BlockTransformer getTransformer(Block block) {
        return TRANSFORMERS.get(block.id());
    }

    public static VanillaInteractionListener getVanillaListener() {
        return interactionListener;
    }

    public static VanillaTiles getVanillaTiles() {
        return vanillaTiles;
    }

    public static Block injectHandler(Block block) {
        return vanillaTiles.injectHandler(block);
    }
}
