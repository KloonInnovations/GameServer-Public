package io.kloon.gameserver.modes.creative.tools.hand;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.minestom.utils.PointFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.blockedits.byhand.CreativeBlockBrokenByHandEvent;
import io.kloon.gameserver.modes.creative.blockedits.byhand.CreativeBlockPlacedByHandEvent;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksJobQueuedEvent;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.util.cooldowns.impl.TickCooldown;
import net.kyori.adventure.text.Component;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;

import java.util.HashSet;
import java.util.Set;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PlayerChangesByHand {
    private final CreativePlayer player;

    private BlockVolumeBuilder before = new BlockVolumeBuilder();
    private BlockVolumeBuilder after = new BlockVolumeBuilder();
    private Set<BlockVec> placed = new HashSet<>();
    private Set<BlockVec> broke = new HashSet<>();

    public static final int DEFAULT_DURATION_TICKS = 3 * 20;
    public static final double DEFAULT_DURATION_SECONDS = DEFAULT_DURATION_TICKS * 0.05;

    private TickCooldown bufferedCooldown = new TickCooldown(DEFAULT_DURATION_TICKS);

    public PlayerChangesByHand(CreativePlayer player) {
        this.player = player;
    }

    public void setBufferingTicks(int ticks) {
        this.bufferedCooldown = new TickCooldown(Math.max(2, ticks));
    }

    public void tick() {
        if (player.getAliveTicks() % 5 != 0) return;

        if (isReadyToFlush()) {
            flush();
        }
    }

    private boolean isReadyToFlush() {
        if (before.isEmpty() && after.isEmpty()) {
            return false;
        }
        return !bufferedCooldown.isOnCooldown();
    }

    public void flush() {
        if (before.isEmpty() && after.isEmpty()) {
            return;
        }
        try {
            Component text = formatChanges();

            player.addToHistory(CreativeToolType.HAND, "<yellow>Hand-Edited World",
                    text, new CoolSound(SoundEvent.BLOCK_MUD_PLACE),
                    new ApplyVolumeChange(before.build(), after.build()));
        } finally {
            clear();
        }
    }

    public void clear() {
        before = new BlockVolumeBuilder();
        after = new BlockVolumeBuilder();
        placed = new HashSet<>();
        broke = new HashSet<>();
        bufferedCooldown.clear();
    }

    private Component formatChanges() {
        int placedCount = placed.size();
        int brokeCount = broke.size();

        if (placedCount == 0 && brokeCount == 0) {
            return MM."<gray>Probably did something!";
        }

        BoundingBox cuboid = before.getCuboid();
        Point center = new BlockVec(BoundingBoxUtils.getCenter(cuboid));

        if (placedCount == 0) {
            return brokeCount == 1
                    ? MM."<gray>Broke a block at \{PointFmt.fmt10k(center)}!"
                    : MM."<gray>Broke \{brokeCount} blocks around \{PointFmt.fmt10k(center)}!";
        }
        if (brokeCount == 0) {
            return placedCount == 1
                    ? MM."<gray>Placed a block at \{PointFmt.fmt10k(center)}!"
                    : MM."<gray>Placed \{placedCount} blocks around \{PointFmt.fmt10k(center)}!";
        }

        String placedPlural = placedCount == 1 ? "block" : "blocks";
        String brokePlural = brokeCount == 1 ? "block" : "blocks";

        return MM."<gray>Placed \{placedCount} \{placedPlural} and broke \{brokeCount} \{brokePlural} around \{PointFmt.fmt10k(center)}!";
    }

    @EventHandler
    public void onPlace(CreativeBlockPlacedByHandEvent event) {
        BlockVec blockVec = event.getBlockPosition();
        before.set(blockVec, Block.AIR);
        after.set(blockVec, event.getBlock());
        bufferedCooldown.cooldown();
        placed.add(blockVec);
        //broke.remove(blockVec); looks better showing breaks AND places
    }

    @EventHandler
    public void onBreak(CreativeBlockBrokenByHandEvent event) {
        BlockVec blockVec = event.getBlockPosition();
        before.set(blockVec, event.getBlock());
        after.set(blockVec, Block.AIR);
        bufferedCooldown.cooldown();
        //placed.remove(blockVec);
        broke.add(blockVec);
    }

    @EventHandler
    public void onInvOpen(InventoryOpenEvent event) {
        flush();
    }

    @EventHandler
    public void onBlocksJob(BlocksJobQueuedEvent event) {
        flush();
    }
}