package io.kloon.gameserver.modes.creative.jobs;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.jobs.work.pasting.PastingWork;
import io.kloon.gameserver.modes.creative.selection.rendering.DisplayCuboid;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.physics.Collisions;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.infra.ranks.StoreRank;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Point;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class BlocksJobQueue {
    private static final Logger LOG = LoggerFactory.getLogger(BlocksJobQueue.class);

    private final CreativeInstance instance;
    private final int maxConcurrentJobs;

    private final AtomicInteger tickNumbers = new AtomicInteger();
    private final List<BlocksJob> jobs = new ArrayList<>();

    public BlocksJobQueue(CreativeInstance instance, int maxConcurrentJobs) {
        this.instance = instance;
        this.maxConcurrentJobs = maxConcurrentJobs;
    }

    @Nullable
    public BlocksJob getJob(int ticketNumber) {
        return jobs.stream().filter(job -> job.getTicketNumber() == ticketNumber).findFirst().orElse(null);
    }

    @Nullable
    public BlocksJob getJob(Point point) {
        return jobs.stream().filter(job -> Collisions.contains(job.getWork().getBoundingBox(), point)).findFirst().orElse(null);
    }

    public List<BlocksJob> getJobs() {
        return jobs;
    }

    @Nullable
    public BlocksJob trySubmit(String name, CreativeToolType toolType, CreativePlayer player, BlocksWork work) {
        if (jobs.size() + 1 > maxConcurrentJobs) {
            player.playSound(SoundEvent.ENTITY_ZOMBIE_AMBIENT, 1.4, 0.6);
            player.sendPit(NamedTextColor.RED, "JOBS FULL", MM."<gray>Couldn't queue job \{name} because there are too many pending jobs!");
            return null;
        }

        BoundingBox boundingBox = work.getBoundingBox();
        List<BlocksJob> colliding = jobs.stream().filter(job -> {
            BlocksWork otherWork = job.getWork();
            return Collisions.intersectBlocks(boundingBox, otherWork.getBoundingBox());
        }).toList();
        if (!colliding.isEmpty()) {
            player.playSound(SoundEvent.ENTITY_ZOMBIE_AMBIENT, 0.89, 0.6);
            if (colliding.size() == 1) {
                BlocksJob collided = colliding.getFirst();
                player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>\{name} would collide with: <white>\{collided.getName()} <dark_gray>(\{collided.getTicketNumber()})");
            } else {
                String jobsFmt = colliding.stream()
                        .map(job -> STR."<white>\{job.getName()} <dark_gray>(\{job.getTicketNumber()})")
                        .collect(Collectors.joining("<gray>, "));
                player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>\{name} would collide with: \{jobsFmt}");
            }
            return null;
        }

        JobOwner owner = JobOwner.fromPlayer(player);
        BlocksJob job = new BlocksJob(name, owner, toolType, tickNumbers.incrementAndGet(), work);
        jobs.add(job);

        int blocksPerTick = computeBlocksPerTick();
        job.setLastBlocksPerTick(blocksPerTick / jobs.size());

        EventDispatcher.call(new BlocksJobQueuedEvent(player, job));

        double eta = job.getETASeconds();
        if (eta > 0.2) {
            player.playSound(SoundEvent.ENTITY_ALLAY_ITEM_GIVEN, 1.9, 0.2);
        }
        if (eta > 2.0) {
            player.msg().send(MsgCat.JOBS, NamedTextColor.GREEN, "JOB STARTED", MM."<gray>Started \{job.getName()}, estimated to take \{NumberFmt.NO_DECIMAL.format(eta)} seconds!");
        }

        return job;
    }

    public void cancel(BlocksJob job) {
        if (jobs.remove(job)) {
            job.cancel();
        }
    }

    public void tryCancelAndNotify(BlocksJob job, CreativePlayer player) {
        if (!player.canEditWorld(true)) {
            return;
        }

        cancel(job);

        player.msg().send(MsgCat.JOBS,
                NamedTextColor.RED, "CANCELLED!", MM."<gray>Cancelled job \{job.getName()}!",
                SoundEvent.ENTITY_ALLAY_DEATH, 1.8, 0.6);
    }

    public void tick() {
        if (jobs.isEmpty()) return;

        int blocksPerTick = computeBlocksPerTick();
        int blocksPerJob = Math.max(1, blocksPerTick / jobs.size());
        for (BlocksJob job : jobs) {
            job.run(blocksPerJob);
        }

        jobs.removeIf(BlocksJob::isEnded);
    }

    private int computeBlocksPerTick() {
        if (jobs.isEmpty()) {
            return 2000;
        }

        boolean hasPremium = instance.streamPlayers()
                .anyMatch(player -> player.getRanks().hasStoreRankOrNewer(StoreRank.EARLY_ADOPTER));
        return hasPremium ? 9001 : 2000;
    }
}
