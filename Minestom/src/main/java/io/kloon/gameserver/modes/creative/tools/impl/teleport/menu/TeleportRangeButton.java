package io.kloon.gameserver.modes.creative.tools.impl.teleport.menu;

import io.kloon.gameserver.MiniMessageTemplate;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.TeleportTool;
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

public class TeleportRangeButton implements ChestButton {
    private final TeleportTool tool;
    private final ItemRef itemRef;

    public static final NumberInput<TeleportTool.Settings> RANGE = NumberInput.consumer(
            Material.CROSSBOW,
            MiniMessageTemplate.TITLE_COLOR,
            "Teleport Range",
            MM_WRAP."Adjust the range at which you teleport when you left click.",
            TeleportTool.DEFAULT_RANGE, TeleportTool.LEFT_CLICK_MIN_RANGE, TeleportTool.LEFT_CLICK_MAX_RANGE,
            s -> s.leftClickRange, (s, value) -> s.leftClickRange = value);

    public TeleportRangeButton(TeleportTool tool, ItemRef itemRef) {
        this.tool = tool;
        this.itemRef = itemRef;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        double min = TeleportTool.LEFT_CLICK_MIN_RANGE;
        double max = TeleportTool.LEFT_CLICK_MAX_RANGE;
        String[] displayLines = SignUX.inputLines("Enter range", min, max);
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(min, max).build(player, rangeInput -> {
            tool.editItemBound(player, itemRef, s -> s.leftClickRange = rangeInput);
            ToolDataType.ITEM_BOUND.sendMsg(player,
                    MM."<gray>Updated teleport range to <green>\{NumberFmt.NO_DECIMAL.format(rangeInput)}<gray>!",
                    SoundEvent.BLOCK_NOTE_BLOCK_PLING, Pitch.range(rangeInput, min, max));
        }));
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Teleport Range";

        List<Component> lore = ToolDataType.ITEM_BOUND.lore();
        lore.addAll(MM_WRAP."<gray>Adjust the range at which you teleport when you left click.");
        lore.add(Component.empty());

        double range = tool.getItemBound(itemRef).leftClickRange;
        if ((int) range == 1) {
            lore.add(MM."<gray>Range: <green>\{NumberFmt.NO_DECIMAL.format(range)} block");
            lore.add(MM."<dark_gray>One small step for man...");
        } else {
            lore.add(MM."<gray>Range: <green>\{NumberFmt.NO_DECIMAL.format(range)} blocks");
        }
        lore.add(Component.empty());

        lore.add(MM."<cta>Click to edit!");

        return MenuStack.of(Material.CROSSBOW).name(name).lore(lore).build();
    }
}
