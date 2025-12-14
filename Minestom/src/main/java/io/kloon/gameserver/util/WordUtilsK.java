package io.kloon.gameserver.util;

import org.apache.commons.text.WordUtils;

public final class WordUtilsK {
    private WordUtilsK() {}

    public static <T extends Enum<T>> String enumName(T enumEntry) {
        return WordUtils.capitalize(enumEntry.name().toLowerCase().replace("_", " "));
    }
}
