package io.kloon.gameserver.minestom.blocks.family;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.*;
import java.util.stream.Collectors;

public class InteractibleFamily {
    private static final Set<InteractibleFamily> FAMILIES = new HashSet<>();
    private static final Map<Block, InteractibleFamily> FAMILY_BY_BLOCK = new HashMap<>();

    private static final Multimap<Feature, InteractibleFamily> FAMILIES_WITH_FEATURE = HashMultimap.create();
    private static final Map<Feature, Set<Block>> BLOCKS_WITH_FEATURE = new HashMap<>();

    public static Set<InteractibleFamily> getAll() {
        return FAMILIES;
    }

    public static InteractibleFamily getByBlock(Block block) {
        return FAMILY_BY_BLOCK.get(block.defaultState());
    }

    public static Collection<InteractibleFamily> getWithFeature(Feature feature) {
        return FAMILIES_WITH_FEATURE.get(feature);
    }

    public static Collection<Block> getBlocksWithFeature(Feature feature) {
        return BLOCKS_WITH_FEATURE.computeIfAbsent(feature, feat -> {
            return FAMILIES_WITH_FEATURE.get(feat).stream()
                    .map(family -> family.getBlock(feat))
                    .collect(Collectors.toSet());
        });
    }

    private final boolean canOpenByHand;

    private Door door;
    private Trapdoor trapdoor;
    private Button button;

    public InteractibleFamily(boolean canOpenByHand) {
        this.canOpenByHand = canOpenByHand;
        FAMILIES.add(this);
    }

    public boolean canOpenByHand() {
        return canOpenByHand;
    }

    public Block getBlock(Feature feature) {
        return switch (feature) {
            case DOOR -> door.block();
            case TRAPDOOR -> trapdoor.block();
            case BUTTON -> button.block();
        };
    }

    private InteractibleFamily door(Door door) {
        this.door = door;
        FAMILY_BY_BLOCK.put(door.block(), this);
        FAMILIES_WITH_FEATURE.put(Feature.DOOR, this);
        return this;
    }

    private InteractibleFamily trapdoor(Trapdoor trapdoor) {
        this.trapdoor = trapdoor;
        FAMILY_BY_BLOCK.put(trapdoor.block(), this);
        FAMILIES_WITH_FEATURE.put(Feature.TRAPDOOR, this);
        return this;
    }

    private InteractibleFamily button(Button button) {
        this.button = button;
        FAMILY_BY_BLOCK.put(button.block(), this);
        FAMILIES_WITH_FEATURE.put(Feature.BUTTON, this);
        return this;
    }

    public record Door(Material material, SoundEvent openSound, SoundEvent closeSound) {
        public Block block() {
            return material.block();
        }
    }

    public record Trapdoor(Material material, SoundEvent openSound, SoundEvent closeSound) {
        public Block block() {
            return material.block();
        }
    }

    public record Button(Material material, SoundEvent onSound, SoundEvent offSound) {
        public Block block() {
            return material.block();
        }
    }

    public enum Feature {
        DOOR,
        TRAPDOOR,
        BUTTON
    }

    public Door door() {
        return door;
    }

    public Trapdoor trapdoor() {
        return trapdoor;
    }

    public Button button() {
        return button;
    }

    public static final InteractibleFamily STONE = new InteractibleFamily(false)
            .button(new Button(Material.STONE_BUTTON, SoundEvent.BLOCK_STONE_BUTTON_CLICK_ON, SoundEvent.BLOCK_STONE_BUTTON_CLICK_OFF))
            ;

    public static final InteractibleFamily POLISHED_BLACKSTONE = new InteractibleFamily(false)
            .button(new Button(Material.POLISHED_BLACKSTONE_BUTTON, SoundEvent.BLOCK_STONE_BUTTON_CLICK_ON, SoundEvent.BLOCK_STONE_BUTTON_CLICK_OFF))
            ;

    public static final InteractibleFamily IRON = new InteractibleFamily(false)
            .door(new Door(Material.IRON_DOOR, SoundEvent.BLOCK_IRON_DOOR_OPEN, SoundEvent.BLOCK_IRON_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.IRON_TRAPDOOR, SoundEvent.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvent.BLOCK_IRON_TRAPDOOR_CLOSE))
            ;

    public static final InteractibleFamily COPPER = new InteractibleFamily(true)
            .door(new Door(Material.COPPER_DOOR, SoundEvent.BLOCK_COPPER_DOOR_OPEN, SoundEvent.BLOCK_COPPER_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.COPPER_TRAPDOOR, SoundEvent.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvent.BLOCK_COPPER_TRAPDOOR_CLOSE))
            ;

    public static final InteractibleFamily WAXED_COPPER = new InteractibleFamily(true)
            .door(new Door(Material.WAXED_COPPER_DOOR, SoundEvent.BLOCK_COPPER_DOOR_OPEN, SoundEvent.BLOCK_COPPER_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.WAXED_COPPER_TRAPDOOR, SoundEvent.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvent.BLOCK_COPPER_TRAPDOOR_CLOSE))
            ;

    public static final InteractibleFamily EXPOSED_COPPER = new InteractibleFamily(true)
            .door(new Door(Material.EXPOSED_COPPER_DOOR, SoundEvent.BLOCK_COPPER_DOOR_OPEN, SoundEvent.BLOCK_COPPER_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.EXPOSED_COPPER_TRAPDOOR, SoundEvent.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvent.BLOCK_COPPER_TRAPDOOR_CLOSE))
            ;

    public static final InteractibleFamily WAXED_EXPOSED_COPPER = new InteractibleFamily(true)
            .door(new Door(Material.WAXED_EXPOSED_COPPER_DOOR, SoundEvent.BLOCK_COPPER_DOOR_OPEN, SoundEvent.BLOCK_COPPER_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.WAXED_EXPOSED_COPPER_TRAPDOOR, SoundEvent.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvent.BLOCK_COPPER_TRAPDOOR_CLOSE))
            ;

    public static final InteractibleFamily WEATHERED_COPPER = new InteractibleFamily(true)
            .door(new Door(Material.WEATHERED_COPPER_DOOR, SoundEvent.BLOCK_COPPER_DOOR_OPEN, SoundEvent.BLOCK_COPPER_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.WEATHERED_COPPER_TRAPDOOR, SoundEvent.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvent.BLOCK_COPPER_TRAPDOOR_CLOSE))
            ;

    public static final InteractibleFamily WAXED_WEATHERED_COPPER = new InteractibleFamily(true)
            .door(new Door(Material.WAXED_WEATHERED_COPPER_DOOR, SoundEvent.BLOCK_COPPER_DOOR_OPEN, SoundEvent.BLOCK_COPPER_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.WAXED_WEATHERED_COPPER_TRAPDOOR, SoundEvent.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvent.BLOCK_COPPER_TRAPDOOR_CLOSE))
            ;

    public static final InteractibleFamily OXIDIZED_COPPER = new InteractibleFamily(true)
            .door(new Door(Material.OXIDIZED_COPPER_DOOR, SoundEvent.BLOCK_COPPER_DOOR_OPEN, SoundEvent.BLOCK_COPPER_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.OXIDIZED_COPPER_TRAPDOOR, SoundEvent.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvent.BLOCK_COPPER_TRAPDOOR_CLOSE))
            ;

    public static final InteractibleFamily WAXED_OXIDIZED_COPPER = new InteractibleFamily(true)
            .door(new Door(Material.WAXED_OXIDIZED_COPPER_DOOR, SoundEvent.BLOCK_COPPER_DOOR_OPEN, SoundEvent.BLOCK_COPPER_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.WAXED_OXIDIZED_COPPER_TRAPDOOR, SoundEvent.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvent.BLOCK_COPPER_TRAPDOOR_CLOSE))
            ;

    public static final InteractibleFamily OAK = standardWood(Material.OAK_DOOR, Material.OAK_TRAPDOOR, Material.OAK_BUTTON);
    public static final InteractibleFamily SPRUCE = standardWood(Material.SPRUCE_DOOR, Material.SPRUCE_TRAPDOOR, Material.SPRUCE_BUTTON);
    public static final InteractibleFamily BIRCH = standardWood(Material.BIRCH_DOOR, Material.BIRCH_TRAPDOOR, Material.BIRCH_BUTTON);
    public static final InteractibleFamily ACACIA = standardWood(Material.ACACIA_DOOR, Material.ACACIA_TRAPDOOR, Material.ACACIA_BUTTON);
    public static final InteractibleFamily JUNGLE = standardWood(Material.JUNGLE_DOOR, Material.JUNGLE_TRAPDOOR, Material.JUNGLE_BUTTON);
    public static final InteractibleFamily DARK_DARK_OAK = standardWood(Material.DARK_OAK_DOOR, Material.DARK_OAK_TRAPDOOR, Material.DARK_OAK_BUTTON);
    public static final InteractibleFamily MANGROVE = standardWood(Material.MANGROVE_DOOR, Material.MANGROVE_TRAPDOOR, Material.MANGROVE_BUTTON);

    private static InteractibleFamily standardWood(Material door, Material trapdoor, Material button) {
        return new InteractibleFamily(true)
                .door(new Door(door, SoundEvent.BLOCK_WOODEN_DOOR_OPEN, SoundEvent.BLOCK_WOODEN_DOOR_CLOSE))
                .trapdoor(new Trapdoor(trapdoor, SoundEvent.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundEvent.BLOCK_WOODEN_TRAPDOOR_CLOSE))
                .button(new Button(button, SoundEvent.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundEvent.BLOCK_WOODEN_BUTTON_CLICK_OFF));
    }

    public static final InteractibleFamily CHERRY = new InteractibleFamily(true)
            .door(new Door(Material.CHERRY_DOOR, SoundEvent.BLOCK_CHERRY_WOOD_DOOR_OPEN, SoundEvent.BLOCK_CHERRY_WOOD_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.CHERRY_TRAPDOOR, SoundEvent.BLOCK_CHERRY_WOOD_TRAPDOOR_OPEN, SoundEvent.BLOCK_CHERRY_WOOD_TRAPDOOR_CLOSE))
            .button(new Button(Material.CHERRY_BUTTON, SoundEvent.BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON, SoundEvent.BLOCK_CHERRY_WOOD_BUTTON_CLICK_OFF))
            ;

    public static final InteractibleFamily CRIMSON = new InteractibleFamily(true)
            .door(new Door(Material.CRIMSON_DOOR, SoundEvent.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundEvent.BLOCK_NETHER_WOOD_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.CRIMSON_TRAPDOOR, SoundEvent.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN, SoundEvent.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE))
            .button(new Button(Material.CRIMSON_BUTTON, SoundEvent.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON, SoundEvent.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF))
            ;

    public static final InteractibleFamily WARPED = new InteractibleFamily(true)
            .door(new Door(Material.WARPED_DOOR, SoundEvent.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundEvent.BLOCK_NETHER_WOOD_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.WARPED_TRAPDOOR, SoundEvent.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN, SoundEvent.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE))
            .button(new Button(Material.WARPED_BUTTON, SoundEvent.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON, SoundEvent.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF))
            ;

    public static final InteractibleFamily BAMBOO = new InteractibleFamily(true)
            .door(new Door(Material.BAMBOO_DOOR, SoundEvent.BLOCK_BAMBOO_WOOD_DOOR_OPEN, SoundEvent.BLOCK_BAMBOO_WOOD_DOOR_CLOSE))
            .trapdoor(new Trapdoor(Material.BAMBOO_TRAPDOOR, SoundEvent.BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN, SoundEvent.BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE))
            .button(new Button(Material.BAMBOO_BUTTON, SoundEvent.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON, SoundEvent.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF))
            ;
}
