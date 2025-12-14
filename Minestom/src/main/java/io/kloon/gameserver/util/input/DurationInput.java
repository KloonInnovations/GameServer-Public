package io.kloon.gameserver.util.input;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationInput {
    private DurationInput() {
    }

    private static final Pattern PERIOD_PATTERN = Pattern.compile("([0-9]+)([hdwmys])");

    // https://stackoverflow.com/a/56395975
    public static long parse(@NotNull String input) {
        input = input.toLowerCase(Locale.ENGLISH);
        Matcher matcher = PERIOD_PATTERN.matcher(input);
        Instant instant = Instant.EPOCH;
        while (matcher.find()) {
            int num = Integer.parseInt(matcher.group(1));
            String typ = matcher.group(2);
            instant = switch (typ) {
                case "s" -> instant.plus(Duration.ofSeconds(num));
                case "m" -> instant.plus(Duration.ofMinutes(num));
                case "h" -> instant.plus(Duration.ofHours(num));
                case "d" -> instant.plus(Duration.ofDays(num));
                case "w" -> instant.plus(Period.ofWeeks(num));
                case "y" -> instant.plus(Period.ofYears(num));
                default -> instant;
            };
        }
        return instant.toEpochMilli();
    }
}
