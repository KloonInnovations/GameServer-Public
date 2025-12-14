package io.kloon.gameserver.modes.creative.blockedits.byhand;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public class CreativeBlockBrokenByHandEvent implements PlayerInstanceEvent, BlockEvent {
    private final CreativePlayer player;
    private final Block blockBefore;
    private final BlockVec blockPosition;
    private final BlockFace blockFace;

    public CreativeBlockBrokenByHandEvent(PlayerBlockBreakEvent event) {
        this.player = (CreativePlayer) event.getPlayer();
        this.blockBefore = event.getBlock();
        this.blockPosition = event.getBlockPosition();
        this.blockFace = event.getBlockFace();
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Block getBlock() {
        return blockBefore;
    }

    @Override
    public @NotNull BlockVec getBlockPosition() {
        return blockPosition;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }
}
