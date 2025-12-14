package io.kloon.gameserver.modes.creative.menu.preferences.messaging;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggle;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggleButton;
import io.kloon.gameserver.modes.creative.storage.playerdata.MessagingStorage;
import io.kloon.gameserver.modes.creative.ux.messaging.MessagingState;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class MessagingPreferencesMenu extends ChestMenu {
    private final ChestMenu parent;
    private final CreativePlayer player;

    public static final PlayerStorageToggle ACTION_BAR_QUEUING = new PlayerStorageToggle(
            Material.STRING, null, "Action Bar Queuing",
            MM_WRAP."<gray>Action bar messages are queued to leave you time to read. Toggle this off to get them all instantly.",
            null,
            s -> s.getMessaging().isActionBarQueueEnabled(),
            (s, value) -> s.getMessaging().setActionBarQueuingEnabled(value));

    public MessagingPreferencesMenu(ChestMenu parent, CreativePlayer player) {
        super("Messaging Preferences", ChestSize.FOUR);
        this.parent = parent;
        this.player = player;
    }

    @Override
    protected void registerButtons() {
        MessagingStorage storage = player.getCreativeStorage().getMessaging();

        Cycle<MessagingState> selfCycle = new Cycle<>(MessagingState.values());
        selfCycle.select(storage.getSelf());
        reg(11, slot -> new CycleButton<>(selfCycle, slot)
                .withIcon(Material.POPPY)
                .withTitle(MM."<title>Own Messages")
                .withDescription(MM_WRAP."<gray>Adjust where your own tool messages appear.")
                .withOnClick((p, state) -> {
                    storage.setSelf(state);

                    player.sendPit(NamedTextColor.GREEN, "MESSAGING", MM."<gray>Set own messages to <white>\{state.label()}<gray>!");
                }));

        Cycle<MessagingState> othersCycle = new Cycle<>(MessagingState.values());
        othersCycle.select(storage.getOthers());
        reg(13, slot -> new CycleButton<>(othersCycle, slot)
                .withIcon(Material.ROSE_BUSH)
                .withTitle(MM."<title>Others' Messages")
                .withDescription(MM_WRAP."<gray>If others can build on your world, adjust where their tool messages go.")
                .withOnClick((p, state) -> {
                    storage.setOthers(state);

                    player.sendPit(NamedTextColor.GREEN, "MESSAGING", MM."<gray>Set others' messages to <white>\{state.label()}<gray>!");
                }));

        reg(15, slot -> new PlayerStorageToggleButton(slot, ACTION_BAR_QUEUING));

        reg().goBack(parent);
    }
}
