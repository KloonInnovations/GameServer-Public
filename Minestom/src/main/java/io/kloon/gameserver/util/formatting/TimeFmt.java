package io.kloon.gameserver.util.formatting;

import humanize.Humanize;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class TimeFmt {
    private static final PrettyTime PRETTY_TIME_WITHOUT_JUST_NOW = new PrettyTime();
    static {
        PRETTY_TIME_WITHOUT_JUST_NOW.removeUnit(JustNow.class);
    }

    public static String date(long timestamp) {
        return date(new Date(timestamp));
    }

    public static String date(long timestamp, String format) {
        Date date = new Date(timestamp);
        return Humanize.formatDate(date, format);
    }

    public static String date(Date date) {
        return Humanize.formatDate(date, "MMM dd hh:mm a");
    }

    public static String date(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd hh:mm a");
        return date.format(formatter);
    }

    public static String naturalTime(long timestamp) {
        return PRETTY_TIME_WITHOUT_JUST_NOW.format(new Date(timestamp));
    }

    public static String singleLetterAgo(long timestamp) {
        long durationMs = Math.abs(System.currentTimeMillis() - timestamp);
        return singleLetterDuration(durationMs);
    }

    public static String singleLetterDuration(long durationMs) {
        long days = TimeUnit.MILLISECONDS.toDays(durationMs);
        if (days >= 1) {
            return days + "d";
        }

        long hours = TimeUnit.MILLISECONDS.toHours(durationMs);
        if (hours >= 1) {
            return hours + "h";
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs);
        if (minutes >= 1) {
            return minutes + "m";
        }

        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs);
        if (seconds >= 1) {
            return seconds + "s";
        }

        return "now";
    }
}
