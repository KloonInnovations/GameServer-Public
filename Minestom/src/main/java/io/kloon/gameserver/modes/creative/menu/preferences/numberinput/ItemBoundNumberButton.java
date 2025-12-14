package io.kloon.gameserver.modes.creative.menu.preferences.numberinput;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ItemBoundNumberButton<ItemBound> extends AbstractNumberInputButton<ItemBound> {
    private static final Logger LOG = LoggerFactory.getLogger(ItemBoundNumberButton.class);

    private final CreativeTool<ItemBound, ?> tool;
    private final ItemRef itemRef;

    private boolean closeOnSet = false;

    public ItemBoundNumberButton(int slot, CreativeTool<ItemBound, ?> tool, ItemRef itemRef, NumberInput<ItemBound> number) {
        super(slot, ToolDataType.ITEM_BOUND, number);
        this.tool = tool;
        this.itemRef = itemRef;
    }

    public ItemBoundNumberButton(int slot, CreativeToolMenu<? extends CreativeTool<ItemBound, ?>> toolMenu, NumberInput<ItemBound> number) {
        super(slot, ToolDataType.ITEM_BOUND, number);
        this.tool = toolMenu.getTool();
        this.itemRef = toolMenu.getItemRef();
    }

    public ItemBoundNumberButton<ItemBound> withCloseOnSet() {
        this.closeOnSet = true;
        return this;
    }

    @Override
    protected double getValue(CreativePlayer player) {
        ItemBound itemBound = tool.getItemBound(itemRef);
        return number.getValue().apply(itemBound);
    }

    public void setValue(CreativePlayer player, double value) {
        try {
            ItemBound itemBound = tool.getItemBound(itemRef);
            double before = number.getValue().apply(itemBound);
            number.editValue().apply(itemBound, value);
            ItemStack modifiedStack = tool.renderItem(itemBound, player);

            boolean changed = itemRef.setIfDidntChange(modifiedStack);
            if (!changed) {
                player.playSound(SoundEvent.ENTITY_ENDERMAN_TELEPORT, 0.6);
                player.sendPit(NamedTextColor.RED, "OOPS", MM."<gray>Couldn't edit your tool because it somehow changed preemptively!");
                player.closeInventory();
                return;
            }

            String colorHex = number.textColor().asHexString();
            onSetValue(player, colorHex, before, value);
        } catch (Throwable t) {
            LOG.error("Error with input handling", t);
            player.sendPitError(MM."<gray>Error while handling your input!");
            player.closeInventory();
        }
    }

    public void onSetValue(CreativePlayer player, String colorHex, double before, double after) {
        player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_BELL, 1.1, 0.7);
        player.sendPit(number.textColor(), number.name(), MM."<gray>Adjusted from \{formatValue(before)} to <\{colorHex}>\{formatValue(after)}<gray>!");
        if (closeOnSet) {
            player.closeInventory();
        }
    }
}
