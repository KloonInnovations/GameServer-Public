package io.kloon.gameserver.modes.creative.tools.impl.copypaste.menu.elems;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteSettings;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipRotation;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class RotateClipButton implements ChestButton {
    private final CopyPasteTool copyPaste;
    private final ItemRef itemRef;
    private final boolean clockwise;

    private static final HeadProfile LEFT_ARROW = SkinCache.toHead("ewogICJ0aW1lc3RhbXAiIDogMTcyMTkwNDMzMzI4MCwKICAicHJvZmlsZUlkIiA6ICIxMTM1Njg1ZTk3ZGE0ZjYyYTliNDQ3MzA0NGFiZjQ0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXJpb1dsZXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhlOTBiNGM1NmU3YmVmOWE5MDgyZTgyZDRlYTY4YzEyODY3YjFiMWMxZjgwMjExZGQyOGYwMTIyMTAwMGUyIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=");
    private static final HeadProfile RIGHT_ARROW = SkinCache.toHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzMzYWU4ZGU3ZWQwNzllMzhkMmM4MmRkNDJiNzRjZmNiZDk0YjM0ODAzNDhkYmI1ZWNkOTNkYThiODEwMTVlMyJ9fX0=");

    public RotateClipButton(CopyPasteTool copyPaste, ItemRef itemRef, boolean clockwise) {
        this.copyPaste = copyPaste;
        this.itemRef = itemRef;
        this.clockwise = clockwise;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        CopyPasteSettings settings = copyPaste.getItemBound(itemRef);
        ClipRotation rotation = settings.getRotation();
        ClipRotation newRotation = clockwise
                ? rotation.clockwise()
                : rotation.counterwise();

        copyPaste.editItemBound(player, itemRef, s -> {
            s.setRotation(newRotation);
        });

        Component msg = clockwise
                ? MM."<gray>90 degrees clockwise, now at \{newRotation.label().toLowerCase()}!"
                : MM."<gray>90 degrees counter-clockwise, now at \{newRotation.label().toLowerCase()}!";
        player.msg().send(MsgCat.TOOL,
                NamedTextColor.GREEN, "ROTATED!", msg,
                SoundEvent.ENTITY_BREEZE_SLIDE, Pitch.rng(1.5, 0.2));

        ChestMenuInv.rerender(player);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CopyPasteSettings settings = copyPaste.getItemBound(itemRef);
        ClipRotation rotation = settings.getRotation();

        Component name = clockwise ? MM."<title>Rotate Clockwise" : MM."<title>Rotate Counter-Clockwise";

        Lore lore = new Lore();
        lore.wrap("<gray>Applies a rotation on the Y axis when pasting.");
        lore.addEmpty();
        lore.add(MM."<gray>Current: <light_purple>\{rotation.label()}");
        lore.addEmpty();
        lore.add("<cta>Click to rotate!");

        ItemBuilder2 builder = MenuStack.ofHead(clockwise ? RIGHT_ARROW : LEFT_ARROW);
        return builder.name(name).lore(lore).build();
    }
}
