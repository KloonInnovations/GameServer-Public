package io.kloon.gameserver.modes.creative.jobs;

import io.kloon.gameserver.modes.creative.tools.CreativeToolType;

import java.util.concurrent.CompletableFuture;

public class BlocksJob {
    private final String name;
    private final JobOwner owner;
    private final CreativeToolType toolType;
    private final int ticketNumber;
    private final BlocksWork work;
    private final CompletableFuture<JobCompletion> future = new CompletableFuture<>();

    private boolean ended;
    private boolean cancelled;

    private int lastBlocksPerTick = 1;

    private long jobStartNanos = System.nanoTime();
    private long cpuNanos;

    public BlocksJob(String name, JobOwner owner, CreativeToolType toolType, int ticketNumber, BlocksWork work) {
        this.name = name;
        this.owner = owner;
        this.toolType = toolType;
        this.ticketNumber = ticketNumber;
        this.work = work;
    }

    public String getName() {
        return name;
    }

    public JobOwner getOwner() {
        return owner;
    }

    public CreativeToolType getToolType() {
        return toolType;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public BlocksWork getWork() {
        return work;
    }

    public boolean isEnded() {
        return ended;
    }

    public boolean wasCancelled() {
        return cancelled;
    }

    public int getETATicks() {
        int todo = work.getTotalToPlace() - work.getPlacedSoFar();
        if (todo <= 0) return 0;
        return (int) Math.ceil((double) todo / lastBlocksPerTick);
    }

    public double getETASeconds() {
        return getETATicks() * 0.05;
    }

    public CompletableFuture<JobCompletion> future() {
        return future;
    }

    public void setLastBlocksPerTick(int blocksPerTick) {
        this.lastBlocksPerTick = blocksPerTick;
    }

    public void run(int blocksToPlace) {
        if (ended || blocksToPlace <= 0) return;
        this.lastBlocksPerTick = blocksToPlace;

        long pieceStartNanos = System.nanoTime();

        int goal = work.getPlacedSoFar() + blocksToPlace;
        try {
            this.ended = work.work(() -> work.getPlacedSoFar() < goal);
        } catch (Throwable t) {
            future.completeExceptionally(t);
            this.ended = true;
            return;
        }

        long elapsedNanos = System.nanoTime() - pieceStartNanos;
        cpuNanos += elapsedNanos;

        if (ended) {
            end(false);
        }
    }

    public void cancel() {
        cancelled = true;
        end(true);
    }

    private void end(boolean cancelled) {
        this.ended = true;
        if (future.isDone()) {
            return;
        }

        long wallNanos = System.nanoTime() - jobStartNanos;
        JobCompletion report = new JobCompletion(cancelled, wallNanos, cpuNanos, work.hadOutOfBounds(), work.getChange());
        future.complete(report);
    }
}
