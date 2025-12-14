package io.kloon.gameserver.modes.creative.tools.impl.copypaste.menu.elems;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.blockvolume.impl.BlockArray;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.VolumeBlockEntity;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClip;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClipDetails;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteSettings;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.bson.types.ObjectId;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ClipboardSlotButton implements ChestButton {
    private final int clipboardIndex;
    private final int slot;

    public ClipboardSlotButton(int clipboardIndex, int slot) {
        this.clipboardIndex = clipboardIndex;
        this.slot = slot;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        if (click.isRightClick()) {
            handleClearSlot(player);
        } else {
            handleLeftClick(player);
        }
    }

    public void handleClearSlot(CreativePlayer player) {
        WorldClip clip = player.getClipboard().getClip(clipboardIndex);
        if (clip == null) {
            return;
        }

        player.getClipboard().remove(clip);
        ChestMenuInv.rerender(player);

        player.msg().send(MsgCat.TOOL,
                NamedTextColor.GREEN, "CLEARED!", MM."<gray>Clipboard slot #\{clipboardIndex + 1}!",
                SoundEvent.ENTITY_BREEZE_SLIDE, Pitch.rng(1.8, 0.2));
    }

    public void handleLeftClick(CreativePlayer player) {
        WorldClip clip = player.getClipboard().getClip(clipboardIndex);
        if (clip == null) {
            return;
        }

        ItemStack inHand = player.getItemInMainHand();
        CopyPasteTool copyPaste = (CopyPasteTool) player.getCreative().getToolsListener().get(CreativeToolType.COPY_PASTE);
        if (copyPaste.isTool(inHand)) {
            ObjectId selectedId = copyPaste.getItemBound(inHand).getClipId();
            if (clip.id().equals(selectedId)) {
                return;
            }

            copyPaste.editItemBound(player, ItemRef.mainHand(player), settings -> {
                settings.setClip(clip);
                ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Set clipboard to #\{clipboardIndex + 1}!",
                        SoundEvent.ENTITY_BREEZE_SLIDE, 0.62f, 0.5f);
            });
            ChestMenuInv.rerender(player);
        } else {
            CopyPasteSettings settings = new CopyPasteSettings();
            settings.setClip(clip);
            copyPaste.giveToPlayer(player, settings);
        }
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<title>Clipboard #\{clipboardIndex + 1}";

        Lore lore = new Lore();
        lore.add(MM."<dark_gray>Player Clipboard");
        lore.addEmpty();

        WorldClip clip = player.getClipboard().getClip(clipboardIndex);
        if (clip == null) {
            lore.add(MM."<gray>Slot is empty!");
            return MenuStack.of(Material.STONE_BUTTON, name, lore);
        }

        WorldClipDetails details = clip.details();
        BoundingBox boundingBox = clip.volume().toCuboid();

        String dimensionsFmt = BoundingBoxUtils.fmtDimensions(boundingBox);
        lore.add(MM."<gray>Dimensions: <green>\{dimensionsFmt}");

        long volume = BoundingBoxUtils.volumeRounded(boundingBox);
        String volumeFmt = NumberFmt.NO_DECIMAL.format(volume);
        if (volume == 1) {
            lore.add(MM."<gray>Volume: <green>Just one block!");
        } else {
            lore.add(MM."<gray>Volume: <green>\{volumeFmt} blocks");
        }
        lore.addEmpty();

        WorldDef worldDef = player.getInstance().getWorldDef();
        if (worldDef._id().equals(details.originWorldId())) {
            lore.add(MM."<gray>From world: <green>This one!");
        } else {
            lore.add(MM."<gray>From world: <white>\{details.originWorldName()}");
        }

        lore.addEmpty();
        lore.add("<rcta>Click to clear slot!");

        ItemBuilder2 builder = MenuStack.of(Material.HEAVY_CORE);

        ItemStack inHand = player.getItemInMainHand();
        CreativeTool tool = player.getCreative().getToolsListener().get(inHand);
        if (tool instanceof CopyPasteTool copyPaste) {
            ObjectId selectedId = copyPaste.getItemBound(inHand).getClipId();
            if (clip.id().equals(selectedId)) {
                builder.glowing();
                lore.add("<green>Tool is using this slot!");
            } else{
                lore.add("<lcta>Click to select slot!");
            }
        } else {
            lore.add("<lcta>Click to grab tool with this slot!");
        }

        return builder.name(name).lore(lore).build();
    }
}
