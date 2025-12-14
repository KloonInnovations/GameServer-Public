package io.kloon.gameserver.minestom.blocks.vanilla.interactions;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public class CustomBlockInteractEvent implements PlayerInstanceEvent, BlockEvent, CancellableEvent {
    private final Player player;
    private final Block block;
    private final BlockVec blockPosition;
    private final Point cursorPosition;
    private final BlockFace blockFace;

    private boolean cancelVanilla;
    private boolean cancelled;
    private boolean blocksItemUse;

    public CustomBlockInteractEvent(PlayerBlockInteractEvent event) {
        this(event.getPlayer(), event.getBlock(), event.getBlockPosition(), event.getCursorPosition(), event.getBlockFace());
    }

    public CustomBlockInteractEvent(Player player, Block block, BlockVec blockPosition, Point cursorPosition, BlockFace blockFace) {
        this.player = player;
        this.block = block;
        this.blockPosition = blockPosition;
        this.cursorPosition = cursorPosition;
        this.blockFace = blockFace;
    }

    @NotNull
    @Override
    public Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public Block getBlock() {
        return block;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    @NotNull
    @Override
    public BlockVec getBlockPosition() {
        return blockPosition;
    }

    public Point getCursorPosition() {
        return cursorPosition;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        this.cancelVanilla = true;
    }

    public boolean isCancelVanilla() {
        return cancelVanilla;
    }

    public void setCancelVanilla(boolean cancelVanilla) {
        this.cancelVanilla = cancelVanilla;
    }

    public boolean isBlockingItemUse() {
        return blocksItemUse;
    }

    public void setBlockingItemUse(boolean blocksItemUse) {
        this.blocksItemUse = blocksItemUse;
    }
}
