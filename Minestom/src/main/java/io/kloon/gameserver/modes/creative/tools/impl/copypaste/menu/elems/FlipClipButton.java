package io.kloon.gameserver.modes.creative.tools.impl.copypaste.menu.elems;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteSettings;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.coordinates.Axis;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.sound.SoundEvent;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class FlipClipButton implements ChestButton {
    private final CopyPasteTool tool;
    private final ItemRef itemRef;
    private final Axis axis;
    private final Function<CopyPasteSettings, Boolean> isFlipped;
    private final BiConsumer<CopyPasteSettings, Boolean> setFlipped;
    private final int slot;

    public FlipClipButton(CopyPasteTool tool, ItemRef itemRef, Axis axis, Function<CopyPasteSettings, Boolean> isFlipped, BiConsumer<CopyPasteSettings, Boolean> setFlipped, int slot) {
        this.tool = tool;
        this.itemRef = itemRef;
        this.axis = axis;
        this.isFlipped = isFlipped;
        this.setFlipped = setFlipped;
        this.slot = slot;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        CopyPasteSettings settings = tool.getItemBound(itemRef);
        boolean flipped = !isFlipped.apply(settings);
        boolean edited = tool.editItemBound(player, itemRef, s -> setFlipped.accept(s, flipped));
        if (edited) {
            player.playSound(SoundEvent.ENTITY_BREEZE_JUMP, Pitch.rng(1.6, 0.2));
            if (flipped) {
                player.msg().send(MsgCat.TOOL, NamedTextColor.GREEN, "FLIPPED!", MM."<gray>On the \{axis.name()} axis!");
            } else {
                player.msg().send(MsgCat.TOOL, NamedTextColor.RED, "UNFLIPPED!", MM."<gray>On the \{axis.name()} axis!");
            }

            ChestMenuInv.rerenderButton(slot, p);
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Flip \{axis.name()} Axis";

        Lore lore = new Lore();
        lore.wrap(MM."<gray>Flips the blocks on the \{axis.name()} axis when pasting.");
        lore.addEmpty();

        CopyPasteSettings settings = tool.getItemBound(itemRef);

        boolean flipped = isFlipped.apply(settings);
        String flippedFmt = flipped ? "<green>Yep!" : "<red>Nope!";
        lore.add(MM."<gray>Flipped: \{flippedFmt}");
        lore.addEmpty();

        lore.add("<cta>Click to toggle!");

        HeadProfile head = SkinCache.toHead(getHeadValue(flipped));
        return MenuStack.ofHead(head).name(name).lore(lore).build();
    }

    private String getHeadValue(boolean enabled) {
        return switch (axis) {
            case X -> enabled
                    ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE0Y2FiYjY5Y2E4ZTUxNDY1YjkzYTQ2NDhkYWEzMWMzYzY0MzA2ZDlkYzM4YzczZWEyMmYyYjNkZTQ0YjUwOCJ9fX0="
                    : "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhjYmRiOTk4ZmI0ZDVlMjg5NjllMjNmNGJiODdjOWRlNWMzMDNmYmViOTBhOTRkOTFkNzQ0NWE2NzNlYTU5ZCJ9fX0=";
            case Y -> throw new NotImplementedException();
            case Z -> enabled
                    ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFiYWMyOGQwMjhhZTA3YjhkOTRhZDY5Nzg4MTg5YTE3MDEwZTY4MmIzNDA4YzU4NjhjNTg2MWI5YmI0M2Q0In19fQ=="
                    : "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ1YTI0YjU0YjdjY2EzYjlmOTliMzM3ZWEwZDExMjliYzYxZmY2YWE4ZGYxNzc4MDY4NmQ4Y2M0OGY1NWYyNCJ9fX0=";
        };
    }
}
