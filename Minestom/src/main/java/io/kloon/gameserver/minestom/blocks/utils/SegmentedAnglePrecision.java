package io.kloon.gameserver.minestom.blocks.utils;

import net.minestom.server.utils.Direction;

public class SegmentedAnglePrecision {
    private final int mask;
    private final int precision;
    private final float degreeToAngle;
    private final float angleToDegree;

    public SegmentedAnglePrecision(int precision) {
        if (precision < 2) {
            throw new IllegalArgumentException("Precision cannot be less than 2 bits");
        }
        if (precision > 30) {
            throw new IllegalArgumentException("Precision cannot be greater than 30 bits");
        }
        int shifted = 1 << precision;
        this.mask = shifted - 1;
        this.precision = precision;
        this.degreeToAngle = (float)shifted / 360.0f;
        this.angleToDegree = 360.0f / (float)shifted;
    }

    public boolean isSameAxis(int $$0, int $$1) {
        int $$2 = this.getMask() >> 1;
        return ($$0 & $$2) == ($$1 & $$2);
    }

//    public int fromDirection(Direction $$0) {
//        if ($$0.getAxis().isVertical()) {
//            return 0;
//        }
//        int $$1 = $$0.get2DDataValue();
//        return $$1 << this.precision - 2;
//    }

    public int fromDegreesWithTurns(float degreesWithTurns) {
        return Math.round(degreesWithTurns * this.degreeToAngle);
    }

    public int fromDegrees(float degrees) {
        return this.normalize(this.fromDegreesWithTurns(degrees));
    }

    public float toDegreesWithTurns(int degrees) {
        return (float)degrees * this.angleToDegree;
    }

    public float toDegrees(int index) {
        float degreesWithTurns = this.toDegreesWithTurns(this.normalize(index));
        return degreesWithTurns >= 180.0f ? degreesWithTurns - 360.0f : degreesWithTurns;
    }

    public int normalize(int index) {
        return index & this.mask;
    }

    public int getMask() {
        return this.mask;
    }
}

