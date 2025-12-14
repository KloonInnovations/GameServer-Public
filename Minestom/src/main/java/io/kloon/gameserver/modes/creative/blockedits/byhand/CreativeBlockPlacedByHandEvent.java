package io.kloon.gameserver.modes.creative.blockedits.byhand;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public class CreativeBlockPlacedByHandEvent implements PlayerInstanceEvent, BlockEvent {
    private final CreativePlayer player;
    private final Block block;
    private final BlockFace blockFace;
    private final BlockVec blockPosition;
    private final PlayerHand hand;

    public CreativeBlockPlacedByHandEvent(PlayerBlockPlaceEvent event) {
        this.player = (CreativePlayer) event.getPlayer();
        this.block = event.getBlock();
        this.blockFace = event.getBlockFace();
        this.blockPosition = event.getBlockPosition();
        this.hand = event.getHand();
    }

    public CreativeBlockPlacedByHandEvent(CreativePlayer player, Block block, BlockFace face, BlockVec blockPos, PlayerHand hand) {
        this.player = player;
        this.block = block;
        this.blockFace = face;
        this.blockPosition = blockPos;
        this.hand = hand;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Block getBlock() {
        return block;
    }

    @Override
    public @NotNull BlockVec getBlockPosition() {
        return blockPosition;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public PlayerHand getHand() {
        return hand;
    }
}
