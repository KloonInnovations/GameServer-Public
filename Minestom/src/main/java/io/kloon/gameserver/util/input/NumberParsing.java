package io.kloon.gameserver.util.input;

public final class NumberParsing {
    private NumberParsing() {}

    public static double parseDouble(String input) throws Exception {
        double num;
        try {
            num = Double.parseDouble(input);
        } catch (Throwable t) {
            throw new Exception("Couldn't parseDouble", t);
        }

        if (!Double.isFinite(num) || Double.isNaN(num)) {
            throw new Exception("Input is not finite");
        }

        return num;
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
