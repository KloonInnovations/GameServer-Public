package io.kloon.gameserver.modes.creative.menu.worldadmin;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.autoupdate.ButtonAutoUpdate;
import io.kloon.gameserver.chestmenus.states.ButtonState;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class KickGuestsFromWorldButton implements ChestButton {
    public static final String ICON = "\uD83E\uDD7E"; // 🥾

    private final int slot;

    public KickGuestsFromWorldButton(int slot) {
        this.slot = slot;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        CreativeInstance instance = player.getInstance();

        ButtonState state = getState(player);
        if (state != CAN_KICK) {
            state.sendChatMessage(player);
            return;
        }

        player.playSound(SoundEvent.ENTITY_CAT_STRAY_AMBIENT, Pitch.base(1.4).addRand(0.2));
        instance.streamPlayers().forEach(cp -> {
            if (cp.canEditWorld()) {
                cp.broadcast().send(MsgCat.WORLD, NamedTextColor.RED, "POOF!", MM."\{player.getDisplayMM()} <gray>kicked all guests from the world!");
                return;
            }
            cp.kick(MM."<red>The world's owner has kicked you!");
        });
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;
        CreativeInstance instance = player.getInstance();

        ButtonAutoUpdate.start(player, this, slot);

        Component name = MM."<red>\{ICON} <title>Kick All Guests";

        Lore lore = new Lore();
        lore.wrap("<gray>Kick all players who aren't allowed to build on your world.");
        lore.addEmpty();

        long onlinePlayers = instance.streamPlayers().count();
        long kickable = instance.streamPlayers().filter(cp -> !cp.canEditWorld()).count();
        lore.add(MM."<gray>Players: <green>\{onlinePlayers} <dark_gray>(includes you!)");
        if (kickable == 0) {
            lore.add(MM."<gray>Guests: <red>None!");
        } else {
            lore.add(MM."<gray>Guests: <aqua>\{kickable}");
        }
        lore.addEmpty();

        ButtonState state = getState(player);
        lore.add(state.getCallToAction(player));

        return MenuStack.of(Material.LEATHER_BOOTS, name, lore);
    }

    private ButtonState getState(CreativePlayer player) {
        CreativeInstance instance = player.getInstance();
        WorldDef worldDef = instance.getWorldDef();
        if (!worldDef.ownership().isOwner(player)) {
            return NOT_WORLD_OWNER;
        }

        long kickable = instance.streamPlayers().filter(p -> !p.canEditWorld()).count();
        if (kickable == 0) {
            return NO_GUESTS;
        }

        return CAN_KICK;
    }

    private static final ButtonState CAN_KICK = new ButtonState("<cta>Click to kick 'em!");
    private static final ButtonState NO_GUESTS = new ButtonState("<!cta>No one to kick!");
    private static final ButtonState NOT_WORLD_OWNER = new ButtonState("<!cta>Not world owner!");
}
