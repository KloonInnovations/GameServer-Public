package io.kloon.gameserver.modes.creative.storage.datainworld.world;

public class CreativeTimeStorage {
    public static final double LOTS_OF_TIME = 12_096_000;
    public static final double DEFAULT_RATE = 1.0;
    public static final double DEFAULT_RATE_PER_SECOND = DEFAULT_RATE * 20;

    private double time = LOTS_OF_TIME;
    private double timeRate = DEFAULT_RATE;

    public double getTime() {
        return time;
    }

    public long getTimeLong() {
        if (time < 0) {
            return (long) (LOTS_OF_TIME + time);
        }

        return (long) time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getTimeRate() {
        return timeRate;
    }

    public void setTimeRate(double timeRate) {
        this.timeRate = timeRate;
    }
}
