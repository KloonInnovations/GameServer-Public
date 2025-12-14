package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.instance.block.Block;

public class WaterBlock {
    public static final BooleanProp WATERLOGGED = new BooleanProp("waterlogged");

    static {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, event -> {
            Block broken = event.getBlock();
            if (!WATERLOGGED.is(broken)) {
                return;
            }

            Block result = event.getResultBlock();
            if (result == Block.AIR) {
                event.setResultBlock(Block.WATER);
            }
        });
    }
}
