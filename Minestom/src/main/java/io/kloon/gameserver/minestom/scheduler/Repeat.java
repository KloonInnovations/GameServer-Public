package io.kloon.gameserver.minestom.scheduler;

import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

import java.util.function.IntConsumer;
import java.util.function.Supplier;

public final class Repeat {
    public static void n(Scheduler scheduler, int n, IntConsumer tickConsumer) {
        n(scheduler, n, 1, tickConsumer);
    }

    public static void n(Scheduler scheduler, int n, int ticksBetween, IntConsumer tickConsumer) {
        scheduler.submitTask(new Supplier<>() {
            private int tick = 0;
            public TaskSchedule get() {
                tickConsumer.accept(tick);

                ++tick;
                if (tick < n) {
                    return TaskSchedule.tick(ticksBetween);
                }
                return TaskSchedule.stop();
            }
        });
    }
}
