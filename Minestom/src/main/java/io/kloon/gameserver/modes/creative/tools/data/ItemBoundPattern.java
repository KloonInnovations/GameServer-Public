package io.kloon.gameserver.modes.creative.tools.data;

import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import org.jetbrains.annotations.Nullable;

public interface ItemBoundPattern {
    boolean hasPattern();

    @Nullable
    CreativePattern getPattern();

    void setPattern(CreativePattern pattern);
}
