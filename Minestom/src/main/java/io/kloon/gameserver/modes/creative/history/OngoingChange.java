package io.kloon.gameserver.modes.creative.history;

import com.spotify.futures.CompletableFutures;
import io.kloon.gameserver.modes.creative.history.builtin.MultiChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.JobCompletion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record OngoingChange(
        long startTimestamp,
        UUID author,
        ChangeMeta meta,
        CompletableFuture<? extends Change> future
) {
    public static OngoingChange fromJob(BlocksJob job, ChangeMeta details) {
        long start = System.currentTimeMillis();
        CompletableFuture<Change> getChange = job.future().thenApply(JobCompletion::change);
        return new OngoingChange(start, job.getOwner().uuid(), details, getChange);
    }

    public static OngoingChange fromJobs(UUID jobOwner, ChangeMeta details, BlocksJob... jobs) {
        return fromJobs(jobOwner, details, Arrays.asList(jobs));
    }

    public static OngoingChange fromJobs(UUID jobOwner, ChangeMeta details, List<BlocksJob> jobs) {
        long start = System.currentTimeMillis();

        List<CompletableFuture<Change>> doJobs = jobs.stream()
                .map(BlocksJob::future)
                .map(completion -> completion.thenApply(JobCompletion::change))
                .toList();
        CompletableFuture<MultiChange> getChanges = CompletableFutures.allAsList(doJobs)
                .thenApply(MultiChange::new);

        return new OngoingChange(start, jobOwner, details, getChanges);
    }

    public static OngoingChange fromJobsAndChanges(UUID jobOwner, ChangeMeta details, List<BlocksJob> jobs, List<Change> extraChanges) {
        long start = System.currentTimeMillis();

        List<CompletableFuture<Change>> doJobs = jobs.stream()
                .map(BlocksJob::future)
                .map(completion -> completion.thenApply(JobCompletion::change))
                .toList();

        CompletableFuture<MultiChange> getChanges = CompletableFutures.allAsList(doJobs)
                .thenApply(jobChanges -> {
                    List<Change> changes = new ArrayList<>(jobChanges);
                    changes.addAll(extraChanges);
                    return new MultiChange(changes);
                });

        return new OngoingChange(start, jobOwner, details, getChanges);
    }
}
