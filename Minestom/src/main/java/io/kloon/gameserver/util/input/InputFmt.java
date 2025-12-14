package io.kloon.gameserver.util.input;

public final class InputFmt {
    private InputFmt() {}

    public static final String CLICK = "\uD83D\uDDB0";
    public static final String LEFT_CLICK = "'\uD83D\uDDB0"; // '🖰
    public static final String RIGHT_CLICK = "\uD83D\uDDB0'"; // 🖰'

    public static final String CLICK_GREEN = "<green>\uD83D\uDDB0";
    public static final String LEFT_CLICK_GREEN = "<green>'\uD83D\uDDB0"; // '🖰
    public static final String RIGHT_CLICK_GREEN = "<green>\uD83D\uDDB0'"; // 🖰'

    public static final String SNEAK = sneak("");
    public static final String SNEAK_CLICK = sneak(CLICK);
    public static final String SNEAK_CLICK_GREEN = sneak(CLICK_GREEN);
    public static final String SNEAK_LCLICK_GREEN = sneak(LEFT_CLICK_GREEN);
    public static final String SNEAK_RCLICK_GREEN = sneak(RIGHT_CLICK_GREEN);

    public static String sneak(String input) {
        return STR."<dark_gray>\uD83D\uDEBC\{input}";
    }

    public static final String KEYBOARD = "\uD83D\uDDAE"; // 🖮
}
