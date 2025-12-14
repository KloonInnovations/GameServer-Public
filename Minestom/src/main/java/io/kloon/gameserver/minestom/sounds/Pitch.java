package io.kloon.gameserver.minestom.sounds;

import java.util.concurrent.ThreadLocalRandom;

public class Pitch {
    private float basePitch = 1.0f;
    private float addRand = 0f;

    private Pitch(float basePitch) {
        this.basePitch = basePitch;
    }

    public float getBasePitch() {
        return basePitch;
    }

    public Pitch addRand(double range) {
        this.addRand = (float) range;
        return this;
    }

    public float compute() {
        float pitch = basePitch;

        if (addRand != 0) {
            ThreadLocalRandom rand = ThreadLocalRandom.current();
            float sign = Math.signum(addRand);
            pitch += sign * rand.nextFloat(Math.abs(addRand));
        }

        return pitch;
    }

    public static Pitch rng(double base, double rng) {
        return Pitch.base(base).addRand(rng);
    }

    public static Pitch base(double basePitch) {
        return new Pitch((float) basePitch);
    }

    public static Pitch range(Number value, Number min, Number max) {
        double increments = (min.doubleValue() / max.doubleValue()) * 1.5;
        double basePitch = 0.5 + value.doubleValue() * increments;
        return new Pitch((float) basePitch);
    }
}
