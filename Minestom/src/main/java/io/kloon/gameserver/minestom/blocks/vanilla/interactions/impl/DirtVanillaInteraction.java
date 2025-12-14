package io.kloon.gameserver.minestom.blocks.vanilla.interactions.impl;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.handlers.FarmlandBlock;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaBlockInteraction;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.util.Set;

public class DirtVanillaInteraction implements VanillaBlockInteraction {
    public static final Set<Material> HOE = Sets.newHashSet(
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLDEN_HOE,
            Material.DIAMOND_HOE,
            Material.NETHERITE_HOE
    );

    @Override
    public boolean handleInteract(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        if (!HOE.contains(player.getItemInMainHand().material())) {
            return false;
        }

        Block farmland = FarmlandBlock.MOISTURE.get(0).on(Block.FARMLAND);
        instance.setBlock(blockPos, farmland);

        return true;
    }
}
