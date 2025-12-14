package io.kloon.gameserver.modes.creative.tools.snipe;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.DropCommand;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipePlayerStorage;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.sound.SoundEvent;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PlayerSnipeHandler {
    private final CreativePlayer player;

    private CreativeTool heldTool;
    private ToolSnipe snipe;

    public PlayerSnipeHandler(CreativePlayer player) {
        this.player = player;
    }

    public double getRange() {
        return player.getCreativeStorage().getSnipe().getRange();
    }

    public BlockVec computeTarget() {
        double range = getRange();
        SnipePlayerStorage storage = player.getCreativeStorage().getSnipe();
        if (storage.isIgnoreBlocks()) {
            return new BlockVec(player.getPointInFront(range));
        } else {
            return player.getTargetBlockPositionOrMax((int) Math.round(range));
        }
    }

    public SnipeVisibility getVisibility() {
        return player.getCreativeStorage().getSnipe().getShapeVisibility();
    }

    public void tick() {
        if (!player.canEditWorld()) {
            remove();
            return;
        }

        ItemStack inHand = player.getItemInMainHand();
        CreativeTool tool = player.getCreative().getToolsListener().get(inHand);

        if (heldTool != tool) {
            this.heldTool = tool;
            if (snipe != null) {
                snipe.remove();
                snipe = null;
            }
            if (tool != null) {
                snipe = tool.createSnipe(player);
            }
        }
        if (snipe == null) {
            return;
        }

        BlockVec target = computeTarget();

        Object itemBound = tool.getItemBound(inHand);
        snipe.tick(target, itemBound);
    }

    public boolean handleDigPacket(ClientPlayerDiggingPacket packet) {
        SnipePlayerStorage storage = player.getCreativeStorage().getSnipe();
        if (!storage.isRangeShortcutEnabled()) {
            return false;
        }

        ItemStack inHand = player.getItemInMainHand();
        CreativeTool toolInHand = player.getCreative().getToolsListener().get(inHand);
        if (toolInHand == null || !toolInHand.usesQSnipe(player)) {
            return false;
        }

        if (packet.status() == ClientPlayerDiggingPacket.Status.DROP_ITEM) {
            cycleRange(storage, true);
            return true;
        } else if(packet.status() == ClientPlayerDiggingPacket.Status.DROP_ITEM_STACK) {
            cycleRange(storage, false);
            return true;
        } else {
            return false;
        }
    }

    private void cycleRange(SnipePlayerStorage storage, boolean forward) {
        double range = storage.getRange();
        List<Double> anchors = storage.getRangeAnchors();
        if (anchors.isEmpty()) {
            player.playSound(SoundEvent.ENTITY_CAT_BEG_FOR_FOOD, 1.3);
            player.sendPit(NamedTextColor.RED, "WHAT?", MM."<gray>Somehow, you don't have any range anchors!");
            return;
        }

        double closestAnchor = anchors.stream().min(Comparator.comparingDouble(anchor -> Math.abs(anchor - range))).get();
        double distToClosest = Math.abs(range - closestAnchor);

        NumberFormat fmt = NumberFmt.NO_DECIMAL;

        int index = anchors.indexOf(closestAnchor);
        double newRange;
        if (distToClosest > 5.0) {
            newRange = anchors.get(index);
            player.msg().send(MsgCat.TOOL, NamedTextColor.GREEN, "Q-RANGE!", MM."<gray>Snapped to \{fmt.format(newRange)} blocks!");
        } else {
            if (forward) {
                newRange = anchors.get((index + 1) % anchors.size());
                player.msg().send(MsgCat.TOOL,
                        NamedTextColor.GREEN, "Q-RANGE!", MM."<gray>Snapped forward to \{fmt.format(newRange)} blocks!"
                                .hoverEvent(MM."<yellow>Click to <green>/\{DropCommand.LABEL} <yellow>your held item!")
                                .clickEvent(ClickEvent.runCommand("/" + DropCommand.LABEL)));
            } else {
                int newIndex = index == 0 ? anchors.size() - 1 : index - 1;
                newRange = anchors.get(newIndex);
                player.msg().send(MsgCat.TOOL,
                        NamedTextColor.GREEN, "Q-RANGE!", MM."<gray>Snapped back to \{fmt.format(newRange)} blocks!"
                                .hoverEvent(MM."<yellow>Click to <green>/\{DropCommand.LABEL} <yellow>your held item!")
                                .clickEvent(ClickEvent.runCommand("/" + DropCommand.LABEL)));
            }
        }

        player.getCreative().getToolsListener().cooldownClick(player);
        storage.setRange(newRange);

        player.getInventory().update();
        playRangeSound(newRange);
    }

    public void playRangeSound(double range) {
        double pitch = 0.5 + (range / SnipePlayerStorage.MAX_RANGE) * 1.5;
        player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_FLUTE, pitch);
    }

    public void remove() {
        if (snipe != null) {
            snipe.remove();
        }
    }
}
