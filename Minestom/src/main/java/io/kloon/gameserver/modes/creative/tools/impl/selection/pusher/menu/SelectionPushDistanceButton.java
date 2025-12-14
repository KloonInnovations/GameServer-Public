package io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.SelectionPusherTool;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class SelectionPushDistanceButton implements ChestButton {
    private final SelectionPusherTool tool;
    private final ItemRef itemRef;

    private final int slot;

    public static final int MIN = 1;
    public static final int MAX = 256;

    public SelectionPushDistanceButton(SelectionPusherTool tool, ItemRef itemRef, int slot) {
        this.tool = tool;
        this.itemRef = itemRef;
        this.slot = slot;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        int current = tool.getItemBound(itemRef).blocks;
        if (current != 1 && click.isRightClick()) {
            tool.editItemBound(player, itemRef, s -> s.blocks = 1);

            ToolDataType.ITEM_BOUND.sendMsg(player,
                    MM."<gray>Updated number of blocks to <green>\{NumberFmt.NO_DECIMAL.format(1)}<gray>!",
                    SoundEvent.BLOCK_NOTE_BLOCK_BANJO, 1);
            ChestMenuInv.rerenderButton(slot, player);
            return;
        }

        int min = MIN;
        int max = MAX;
        String[] displayLines = SignUX.inputLines("Moving blocks", min, max);
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(min, max).build(player, input -> {
            tool.editItemBound(player, itemRef, s -> s.blocks = input.intValue());

            ToolDataType.ITEM_BOUND.sendMsg(player,
                    MM."<gray>Updated number of blocks to <green>\{NumberFmt.NO_DECIMAL.format(input)}<gray>!",
                    SoundEvent.BLOCK_NOTE_BLOCK_PLING, Pitch.range(input, min, 32));
        }));
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Number of Blocks";

        List<Component> lore = ToolDataType.ITEM_BOUND.lore();
        lore.addAll(MM_WRAP."<gray>How many blocks to push the selection by.");
        lore.add(Component.empty());

        int blocks = tool.getItemBound(itemRef).blocks;
        Component msg = blocks == 1
                ? MM."<gray>Moving by: <green>\{NumberFmt.NO_DECIMAL.format(blocks)} block"
                : MM."<gray>Moving by: <green>\{NumberFmt.NO_DECIMAL.format(blocks)} blocks";
        lore.add(msg);
        lore.add(Component.empty());

        if (blocks == 1) {
            lore.add(MM."<cta>Click to edit!");
        } else {
            lore.add(MM."<rcta>Click to set to 1!");
            lore.add(MM."<lcta>Click to edit!");
        }

        return MenuStack.of(Material.TRIDENT).name(name).lore(lore).build();
    }
}
