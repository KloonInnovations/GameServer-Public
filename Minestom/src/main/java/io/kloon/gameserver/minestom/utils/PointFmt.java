package io.kloon.gameserver.minestom.utils;

import io.kloon.gameserver.util.formatting.NumberFmt;
import net.minestom.server.coordinate.Point;

import java.text.NumberFormat;

public final class PointFmt {
    public PointFmt() {}

    public static String fmtVec(Point point) {
        NumberFormat nf = NumberFmt.TWO_DECIMAL;
        return "(" + nf.format(point.x()) + ", " + nf.format(point.y()) + ", " + nf.format(point.z()) + ")";
    }

    public static String fmtBlock(Point point) {
        return "(" + point.blockX() + ", " + point.blockY() + ", " + point.blockZ() + ")";
    }

    public static String fmt10k(Point point) {
        return "(" + fmt10k(point.blockX()) + ", " + point.blockY() + ", " + fmt10k(point.blockZ()) + ")";
    }

    public static String fmt10k(double val) {
        int k = (int) val / 1000;
        int remainder = (int) (val % 1000);
        return k + "k" + remainder;
    }
}
