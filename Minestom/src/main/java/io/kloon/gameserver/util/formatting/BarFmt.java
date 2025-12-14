package io.kloon.gameserver.util.formatting;

public final class BarFmt {
    private BarFmt() {}

    public static String renderBar(int length, double percent) {
        StringBuilder sb = new StringBuilder();

        sb.append("<#FF266E><st>");
        long done = Math.round(length * percent);
        for (int i = 0; i < done; ++i) {
            sb.append("-");
        }
        sb.append("</st></#FF266E>");

        sb.append("<#A3266E>");
        long missing = Math.round(length * (1 - percent));
        for (int i = 0; i < missing; ++i) {
            sb.append("-");
        }
        sb.append("</#A3266E>");

        return sb.toString();
    }
}
