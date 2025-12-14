package io.kloon.gameserver.modes.creative.tools;

import io.kloon.gameserver.minestom.blocks.vanilla.interactions.CustomBlockInteractEvent;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.snap.BlockSelectionUtils;
import io.kloon.gameserver.modes.creative.tools.click.ToolClickSide;
import io.kloon.gameserver.modes.creative.tools.click.impl.AirToolClick;
import io.kloon.gameserver.modes.creative.tools.click.impl.BlockToolClick;
import io.kloon.gameserver.modes.creative.tools.click.impl.InventoryToolClick;
import io.kloon.gameserver.modes.creative.tools.generics.NotImplementedTool;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ToolsListener {
    private static final Logger LOG = LoggerFactory.getLogger(ToolsListener.class);
    
    private final Map<CreativeToolType, CreativeTool> toolsByType = new HashMap<>();
    private final Map<String, CreativeTool> toolsByDbKey = new HashMap<>();
    private final List<TickingTool> tickingTools = new ArrayList<>();

    private final PlayerTickCooldownMap clickCd = new PlayerTickCooldownMap(2);

    public void register(CreativeTool tool) {
        CreativeToolType toolType = tool.getType();
        toolsByType.put(toolType, tool);
        toolsByDbKey.put(toolType.getDbKey(), tool);
        if (tool instanceof TickingTool ticking) {
            tickingTools.add(ticking);
        }
    }

    public CreativeTool get(CreativeToolType type) {
        return toolsByType.getOrDefault(type, new NotImplementedTool(type.getDbKey()));
    }

    public Collection<CreativeTool> getAll() {
        return Collections.unmodifiableCollection(toolsByType.values());
    }

    public void cooldownClick(CreativePlayer player) { // mostly a hack, because drop is right before anim
        clickCd.get(player).cooldown();
    }

    public void tickForPlayer(CreativePlayer player) {
        ItemStack inHand = player.getItemInMainHand();
        CreativeTool toolInHand = get(inHand);
        if (toolInHand instanceof TickingTool tickingInHand) {
            try {
                tickingInHand.tickHolding(player, inHand);
            } catch (Throwable t) {
                LOG.error(STR."Error ticking tool in hand \{toolInHand.getType()} for \{player}", t);
            }
        }

        tickingTools.forEach(tickingTool -> {
            if (toolInHand == tickingTool) return;
            try {
                tickingTool.tickWithoutHolding(player);
            } catch (Throwable t) {
                LOG.error(STR."Error ticking tool NOT held \{tickingTool.getType()} for \{player}", t);
            }
        });
    }

    @Nullable
    private CreativeTool getToolInHand(Player player) {
        return get(player.getItemInMainHand());
    }

    @Nullable
    public CreativeTool get(ItemStack item) {
        String toolDbKey = item.getTag(CreativeTool.TOOL_TYPE_TAG);
        return toolsByDbKey.get(toolDbKey);
    }

    @EventHandler
    public void onAnim(PlayerHandAnimationEvent event) {
        CreativePlayer player = (CreativePlayer) event.getPlayer();
        CreativeTool tool = getToolInHand(player);
        if (tool == null) return;

        ItemStack item = player.getItemInMainHand();

        if (clickCd.get(player).cooldownIfPossible()) {
            Vec blockVec = tool.usesQSnipe(player)
                    ? player.getSnipe().computeTarget().asVec()
                    : BlockSelectionUtils.findSelectedBlock(player);
            BlockToolClick click = new BlockToolClick(ToolClickSide.LEFT, item, blockVec, null);
            tool.handleClick(player, click);
        }
    }

    @EventHandler
    public void onPlace(PlayerBlockPlaceEvent event) {
        clickCd.get(event.getPlayer()).cooldown();
    }

    @EventHandler
    public void onBreak(PlayerBlockBreakEvent event) {
        CreativePlayer player = (CreativePlayer) event.getPlayer();
        CreativeTool tool = getToolInHand(player);
        if (tool == null) return;

        event.setCancelled(true);

        ItemStack item = player.getItemInMainHand();

        if (clickCd.get(player).cooldownIfPossible()) {
            Vec blockVec = Vec.fromPoint(event.getBlockPosition());
            BlockToolClick click = new BlockToolClick(ToolClickSide.LEFT, item, blockVec, null);
            tool.handleClick(player, click);
        }
    }

    @EventHandler
    public void onUseAir(PlayerUseItemEvent event) {
        CreativePlayer player = (CreativePlayer) event.getPlayer();
        CreativeTool tool = getToolInHand(player);
        if (tool == null) return;

        event.setCancelled(true);

        if (clickCd.get(player).cooldownIfPossible()) {
            AirToolClick click = new AirToolClick(ToolClickSide.RIGHT, event.getItemStack());
            tool.handleClick(player, click);
        }
    }

    @EventHandler
    public void onUseOnBlock(CustomBlockInteractEvent event) {
        CreativePlayer player = (CreativePlayer) event.getPlayer();
        ItemStack item = event.getPlayer().getItemInMainHand();
        CreativeTool tool = get(item);
        if (tool == null) return;

        event.setCancelled(true);
        event.setBlockingItemUse(true);

        if (clickCd.get(player).cooldownIfPossible()) {
            Vec cursorPos = Vec.fromPoint(event.getCursorPosition());
            BlockToolClick click = new BlockToolClick(ToolClickSide.RIGHT, item, event.getBlockPosition(), cursorPos);
            tool.handleClick(player, click);
        }
    }

    @EventHandler
    public void onClickItem(InventoryPreClickEvent event) {
        CreativePlayer player = (CreativePlayer) event.getPlayer();
        CreativeTool tool = get(event.getClickedItem());
        if (tool == null) return;

        InventoryToolClick click = new InventoryToolClick(ToolClickSide.LEFT, event.getClickedItem(), event);
        tool.handleClickInInventory(player, click);
    }
}
