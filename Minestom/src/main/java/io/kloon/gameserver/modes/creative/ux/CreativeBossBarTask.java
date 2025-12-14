package io.kloon.gameserver.modes.creative.ux;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.gameserver.minestom.color.ColorUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.BlocksJobQueue;
import io.kloon.gameserver.util.RandUtil;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;

import java.util.*;
import java.util.stream.Collectors;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreativeBossBarTask {
    private final CreativeInstance instance;
    private final BlocksJobQueue jobsQueue;

    private final Map<BlocksJob, BossBar> barPerJob = new HashMap<>();

    public CreativeBossBarTask(CreativeInstance instance, BlocksJobQueue jobsQueue) {
        this.instance = instance;
        this.jobsQueue = jobsQueue;
    }

    public void tick() {
        if (GlobalMinestomTicker.getTick() % 3 != 0) return;

        cleanupDeadJobs();

        jobsQueue.getJobs().forEach(job -> {
            if (job.getETASeconds() < 1) return;
            barPerJob.computeIfAbsent(job, j -> {
                BossBar.Color color = pickColor();
                float percent = getJobPercent(job);
                Component name = getBarName(job, color);
                return BossBar.bossBar(name, percent, color, BossBar.Overlay.PROGRESS);
            });
        });

        barPerJob.forEach((job, bar) -> {
            Component name = getBarName(job, bar.color());
            bar.name(name);

            bar.progress(getJobPercent(job));

            instance.showBossBar(bar);
        });
    }

    private Component getBarName(BlocksJob job, BossBar.Color color) {
        TextColor nameColor = ColorUtils.BOSS_BAR_TO_TEXT_COLOR.get(color);
        String nameColorHex = nameColor.asHexString();

        String secondsFmt = NumberFmt.ONE_DECIMAL.format(job.getETASeconds()) + "s";

        float percent = getJobPercent(job);
        String percentFmt = NumberFmt.NO_DECIMAL.format(percent * 100) + "%";

        return MM."<dark_gray>(\{job.getTicketNumber()}) <\{nameColorHex}>\{job.getName()} <#FFE36B>\{secondsFmt} <#FF266E>\{percentFmt}";
    }

    private float getJobPercent(BlocksJob job) {
        BlocksWork work = job.getWork();
        int placed = work.getPlacedSoFar();
        int total = work.getTotalToPlace();
        if (total <= 0) return 1.0f;
        return Math.min(1.0f, (float) placed / total);
    }

    private void cleanupDeadJobs() {
        barPerJob.entrySet().removeIf(entry -> {
            BlocksJob job = entry.getKey();
            BossBar bar = entry.getValue();

            boolean remove = job.isEnded();
            if (remove) {
                MinecraftServer.getBossBarManager().destroyBossBar(bar);
            }
            return remove;
        });
    }

    private static final Set<BossBar.Color> BB_COLORS = Set.of(BossBar.Color.values());
    private BossBar.Color pickColor() {
        Set<BossBar.Color> existingColors = barPerJob.values().stream().map(BossBar::color).collect(Collectors.toSet());
        Set<BossBar.Color> missingColors = Sets.difference(BB_COLORS, existingColors);
        if (missingColors.isEmpty()) {
            missingColors = BB_COLORS;
        }
        return RandUtil.getRandom(new ArrayList<>(missingColors));
    }
}
