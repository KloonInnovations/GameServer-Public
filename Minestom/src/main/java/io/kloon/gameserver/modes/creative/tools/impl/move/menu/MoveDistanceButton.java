package io.kloon.gameserver.modes.creative.tools.impl.move.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.ItemBoundNumberButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.impl.move.MoveTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class MoveDistanceButton implements ChestButton {
    public static final NumberInput<MoveTool.Settings> DISTANCE_BLOCKS = NumberInput.consumerInt(
            Material.TRIDENT, NamedTextColor.AQUA,
            "Move Distance (Blocks)",
            MM_WRAP."<gray>How many blocks to move by.",
            1, 1, 180,
            s -> s.blocks, (s, value) -> s.blocks = value);

    public static final NumberInput<MoveTool.Settings> DISTANCE_MULTIPLES = NumberInput.consumerInt(
            Material.RAW_COPPER, NamedTextColor.GREEN,
            "Move Distance (Multiples)",
            MM_WRAP."<gray>How much to move by, in multiples of the length of the selection on the movement axis.",
            1, 1, 32,
           s -> s.multiples, (s, value) -> s.multiples = value);

    private final int slot;
    private final CreativeToolMenu<MoveTool> menu;

    private final ItemBoundNumberButton<MoveTool.Settings> blocksButton;
    private final ItemBoundNumberButton<MoveTool.Settings> multiplesButton;

    public MoveDistanceButton(int slot, CreativeToolMenu<MoveTool> menu) {
        this.slot = slot;
        this.menu = menu;

        this.blocksButton = new ItemBoundNumberButton<>(slot, menu, DISTANCE_BLOCKS) {
            protected Lore createExtraLore(CreativePlayer player, double playerValue) {
                return new Lore().addEmpty().add("<rcta>Click to switch to multiples!");
            }
            protected boolean canRightClickToReset() {
                return false;
            }
        };
        this.multiplesButton = new ItemBoundNumberButton<>(slot, menu, DISTANCE_MULTIPLES) {
            protected Lore createExtraLore(CreativePlayer player, double playerValue) {
                return new Lore().addEmpty().add("<rcta>Click to switch to blocks!");
            }
            protected boolean canRightClickToReset() {
                return false;
            }
        };
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        MoveTool.Settings settings = menu.getTool().getItemBound(menu.getItemRef());
        if (click.isRightClick()) {
            settings.moveMultiples = !settings.moveMultiples;
            menu.getTool().editItemBound(player, menu.getItemRef(), s -> s.moveMultiples = settings.moveMultiples);

            player.playSound(SoundEvent.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON, settings.moveMultiples ? 1.1 : 1.4);
            if (settings.moveMultiples) {
                ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Moving selection using multiples of size!");
            } else {
                ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Moving selection by block count!");
            }
            ChestMenuInv.rerenderButton(slot, player);
            return;
        }

        if (settings.moveMultiples) {
            multiplesButton.clickButton(player, click);
        } else {
            blocksButton.clickButton(player, click);
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        MoveTool.Settings settings = menu.getTool().getItemBound(menu.getItemRef());
        if (settings.moveMultiples) {
            return multiplesButton.renderButton(player);
        } else {
            return blocksButton.renderButton(player);
        }
    }
}
