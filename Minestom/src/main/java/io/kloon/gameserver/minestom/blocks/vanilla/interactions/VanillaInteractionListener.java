package io.kloon.gameserver.minestom.blocks.vanilla.interactions;

import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VanillaInteractionListener {
    private final Map<Block, VanillaBlockInteraction> interactions = new HashMap<>();

    public void register(Block block, VanillaBlockInteraction interaction) {
        interactions.put(block, interaction);
    }

    @Nullable
    public VanillaBlockInteraction get(Block block) {
        return interactions.get(block.defaultState());
    }

    public void handleEvent(PlayerBlockInteractEvent event) {
        if (event.isCancelled()) return;
        if (event.getHand() != PlayerHand.MAIN) return;

        Block block = event.getBlock();
        VanillaBlockInteraction vanillaInteraction = get(block);

        CustomBlockInteractEvent customEvent = new CustomBlockInteractEvent(event);
        EventDispatcher.call(customEvent);

        event.setBlockingItemUse(customEvent.isBlockingItemUse());
        event.setCancelled(customEvent.isCancelled());

        if (vanillaInteraction != null && !customEvent.isCancelVanilla()) {
            block = event.getInstance().getBlock(event.getBlockPosition());
            boolean handled = vanillaInteraction.handleInteract(event.getPlayer(), event.getInstance(), event.getBlockPosition(), event.getCursorPosition(), block);
            if (handled) {
                event.setBlockingItemUse(true);
            }
        }
    }
}
