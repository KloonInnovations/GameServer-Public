package io.kloon.gameserver.modes.creative.commands.tools.api.give;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.patterns.PatternParseException;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class ToolItemExecutor<ItemBound, PlayerBound> extends CreativeExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(ToolItemExecutor.class);

    private final CreativeTool<ItemBound, PlayerBound> tool;
    private final CreativeToolType toolType;
    private final ToolDataType dataType;

    public ToolItemExecutor(CreativeTool<ItemBound, PlayerBound> tool, ToolDataType dataType) {
        this.tool = tool;
        this.toolType = tool.getType();
        this.dataType = dataType;
    }

    @Override
    public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
        boolean canEdit = player.canEditWorld();

        CreativeToolType toolType = tool.getType();
        if (!toolType.canInstantiate()) {
            player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>You cannot spawn/edit this tool using this command!");
            return;
        }

        if (!canEdit && !toolType.isAvailableWithoutBuildPerms()) {
            player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>This tool is unavailable without edit permissions!");
            return;
        }

        ItemStack inHand = player.getItemInMainHand();
        if (tool.isTool(inHand)) {
            ItemRef itemRef = ItemRef.mainHand(player);
            edit(player, itemRef, context);
        } else {
            give(player, context);
        }
    }

    public void give(CreativePlayer player, CommandContext context) {
        ItemStack toolStack;
        try {
            ItemBound itemBound = tool.createDefaultItemBound();
            PlayerBound playerBound = tool.getPlayerBound(player);

            modifyToolData(player, itemBound, playerBound, context);

            if (dataType == ToolDataType.PLAYER_BOUND) {
                player.getToolsStorage().set(tool.getType(), playerBound);
            }

            toolStack = tool.renderItem(itemBound, playerBound);
        } catch (PatternParseException e) {
            e.sendError(player);
            return;
        } catch (Throwable t) {
            LOG.error("Error modifying tool data for command!", t);
            player.sendPitError(MM."<gray>While running this tool command!");
            return;
        }

        int slot = player.getInventoryExtras().addToHotbar(toolStack);
        if (slot >= 0) {
            player.setHeldItemSlot((byte) slot);

            player.msg().send(MsgCat.INVENTORY,
                    NamedTextColor.GREEN, "COMMANDED!", MM."<gray>\{toolType.getDisplayName()} added to hotbar!",
                    SoundEvent.BLOCK_AMETHYST_CLUSTER_STEP, 1.0);
        } else {
            player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 1.0);
            player.sendPit(NamedTextColor.RED, "EEK!", MM."<gray>Couldn't add \{toolType.getDisplayName()} to your hotbar!");
        }
    }

    public void edit(CreativePlayer player, ItemRef itemRef, CommandContext context) {
        ItemStack toolStack;
        ToolEditFx effect;
        try {
            ItemBound itemBound = tool.getItemBound(itemRef);
            PlayerBound playerBound = tool.getPlayerBound(player);

            modifyToolData(player, itemBound, playerBound, context);

            if (dataType == ToolDataType.PLAYER_BOUND) {
                player.getToolsStorage().set(tool.getType(), playerBound);
            }

            toolStack = tool.renderItem(itemBound, playerBound);
            effect = createEditFx(player, itemBound, playerBound, context);
        } catch (Throwable t) {
            LOG.error("Error modifying tool data for command!", t);
            player.sendPitError(MM."<gray>While running this tool command!");
            return;
        }

        boolean edited = itemRef.setIfDidntChange(toolStack);
        if (!edited) {
            player.playSound(SoundEvent.ENTITY_ENDERMAN_TELEPORT, 0.6);
            player.sendPit(NamedTextColor.RED, "OOPS", MM."<gray>Couldn't edit your tool because it somehow changed preemptively!");
            return;
        }

        dataType.sendPit(player, effect.details());
        player.playSound(effect.sound(), effect.pitch());
    }

    public abstract void modifyToolData(CreativePlayer player, ItemBound settings, PlayerBound pref, CommandContext context) throws PatternParseException;

    public abstract ToolEditFx createEditFx(CreativePlayer player, ItemBound settings, PlayerBound pref, CommandContext context);
}