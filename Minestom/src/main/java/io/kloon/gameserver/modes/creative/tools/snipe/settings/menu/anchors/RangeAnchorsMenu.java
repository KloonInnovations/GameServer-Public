package io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.anchors;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggle;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggleButton;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipePlayerStorage;
import net.minestom.server.item.Material;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class RangeAnchorsMenu extends ChestMenu {
    private final ChestMenu parent;
    private final CreativePlayer player;

    public static final PlayerStorageToggle RANGE_SHORTCUT_ENABLED = new PlayerStorageToggle(
            Material.SOUL_CAMPFIRE, null, "Q-Range Toggle",
            MM_WRAP."<gray>Whether the Q (drop) feature is enabled at all.",
            null,
            storage -> storage.getSnipe().isRangeShortcutEnabled(),
            (storage, value) -> storage.getSnipe().setRangeShortcutEnabled(value));

    public RangeAnchorsMenu(ChestMenu parent, CreativePlayer player) {
        super("Q-Range Anchors", ChestSize.FOUR);
        this.parent = parent;
        this.player = player;
    }

    @Override
    protected void registerButtons() {
        List<Double> anchors = player.getCreativeStorage().getSnipe().getRangeAnchors();

        for (int i = 0; i < anchors.size(); ++i) {
            double range = anchors.get(i);
            int slot = 10 + i;
            reg(slot, new RangeAnchorButton(this, i, range));
        }
        if (anchors.size() < SnipePlayerStorage.MAX_ANCHORS) {
            int lastSlot = 10 + anchors.size();
            reg(lastSlot, new AddRangeAnchorButton(this));
        }

        reg().goBack(parent);
        reg(size.bottomCenter() + 1, slot -> new PlayerStorageToggleButton(slot, RANGE_SHORTCUT_ENABLED));
    }
}
