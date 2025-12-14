package io.kloon.gameserver.modes.creative.blockedits.authorization;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockEditAuthorizationEvent implements PlayerInstanceEvent, BlockEvent {
    private final CreativePlayer player;
    private final Block block;
    private final BlockVec blockPosition;

    private final List<BlockEditDenial> denials = new ArrayList<>();

    public BlockEditAuthorizationEvent(CreativePlayer player, Block block, BlockVec blockPosition) {
        this.player = player;
        this.block = block;
        this.blockPosition = blockPosition;
    }

    public <T extends PlayerInstanceEvent & BlockEvent> BlockEditAuthorizationEvent(T event) {
        this.player = (CreativePlayer) event.getPlayer();
        this.block = event.getBlock();
        this.blockPosition = event.getBlockPosition();
    }

    @Override
    public @NotNull Block getBlock() {
        return block;
    }

    @Override
    public @NotNull BlockVec getBlockPosition() {
        return blockPosition;
    }

    @Override
    public @NotNull CreativePlayer getPlayer() {
        return player;
    }

    public void add(BlockEditDenial authorization) {
        denials.add(authorization);
    }

    public void deny(BlockEditDenial.Source source, Runnable callbackIfBlocker) {
        denials.add(new BlockEditDenial(source, callbackIfBlocker));
    }

    public List<BlockEditDenial> getDenials() {
        return Collections.unmodifiableList(denials);
    }
}
