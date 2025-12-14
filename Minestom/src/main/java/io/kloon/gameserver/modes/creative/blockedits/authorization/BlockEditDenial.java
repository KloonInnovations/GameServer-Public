package io.kloon.gameserver.modes.creative.blockedits.authorization;

public record BlockEditDenial(Source source, Runnable callbackIfBlocker) {
    // most senior aka highest priority is LAST
    public enum Source {
        PART_OF_JOB,
        WORLD_BORDERS,
        NO_PERMIT
    }
}
