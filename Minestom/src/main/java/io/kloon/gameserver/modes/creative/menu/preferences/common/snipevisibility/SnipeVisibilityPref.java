package io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility;

import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;

public interface SnipeVisibilityPref {
    SnipeVisibility getSnipeVisibility();

    void setSnipeVisibility(SnipeVisibility visibility);
}
