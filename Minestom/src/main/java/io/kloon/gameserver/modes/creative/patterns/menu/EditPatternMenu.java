package io.kloon.gameserver.modes.creative.patterns.menu;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;

public abstract class EditPatternMenu<T extends CreativePattern> extends ChestMenu {
    protected final ChestMenu parent;
    private final CreativeConsumer<CreativePattern> update;

    protected T pattern;

    public EditPatternMenu(ChestMenu parent, T pattern, CreativeConsumer<CreativePattern> update) {
        super(STR."Edit \{pattern.getType().getName()}");
        this.parent = parent;
        this.pattern = pattern;
        this.update = update;
    }

    public T getPattern() {
        return pattern;
    }

    public void updateAndDisplay(CreativePlayer player, T updatedPattern) {
        this.pattern = updatedPattern;
        update.accept(player, updatedPattern);
        reload().display(player);
    }
}
