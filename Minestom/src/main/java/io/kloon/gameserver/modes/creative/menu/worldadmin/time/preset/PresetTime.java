package io.kloon.gameserver.modes.creative.menu.worldadmin.time.preset;

public enum PresetTime {
    MORNING("Morning", 0, "Pretty much when the sun stops being tired and wakes up."),
    DAY("Day", 6000, "Mid day, when you need to apply sunscreen."),
    EVENING("Evening", 12000, "When the sun is setting down, best time for Instagram pictures."),
    NIGHT("Night", 18000, "Zzz... Zzz..."),
    ;

    private final String label;
    private final int mcTime;
    private final String description;

    PresetTime(String label, int mcTime, String description) {
        this.label = label;
        this.mcTime = mcTime;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public int getMcTime() {
        return mcTime;
    }

    public String getDescription() {
        return description;
    }
}
