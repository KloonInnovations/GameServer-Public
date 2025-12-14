package io.kloon.gameserver.modes.creative.commands.jobs;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.BlocksJobQueue;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CancelJobCommand extends Command {
    public static final String LABEL = "cancel";

    public CancelJobCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                cancelLatestJob(player);
            }
        });

        ArgumentInteger jobNumberArg = ArgumentType.Integer("job number");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                int jobNumber = context.get(jobNumberArg);
                cancelJob(player, jobNumber);
            }
        }, jobNumberArg);
    }

    public static void cancelJob(CreativePlayer player, int jobNumber) {
        BlocksJobQueue jobQueue = player.getInstance().getJobQueue();
        BlocksJob job = jobQueue.getJob(jobNumber);
        if (job == null || job.isEnded()) {
            player.sendPit(NamedTextColor.RED, "NOT FOUND", MM."<gray>Couldn't find an ongoing job with this number!");
            return;
        }

        jobQueue.tryCancelAndNotify(job, player);
    }

    @Nullable
    public static BlocksJob getLatestIncompleteJob(CreativePlayer player) {
        BlocksJobQueue jobQueue = player.getInstance().getJobQueue();
        List<BlocksJob> jobs = jobQueue.getJobs();
        return jobs.stream()
                .filter(job -> job.getOwner().is(player) && !job.isEnded())
                .max(Comparator.comparingLong(BlocksJob::getTicketNumber))
                .orElse(null);
    }

    public static void cancelLatestJob(CreativePlayer player) {
        BlocksJob latestJob = getLatestIncompleteJob(player);

        if (latestJob == null) {
            player.playSound(SoundEvent.ENTITY_ZOMBIE_HORSE_HURT, 0.8, 0.7);
            player.sendPit(NamedTextColor.RED, "NOT FOUND!", MM."<gray>You don't have any ongoing jobs!");
            return;
        }

        BlocksJobQueue jobQueue = player.getInstance().getJobQueue();
        jobQueue.tryCancelAndNotify(latestJob, player);
    }
}
