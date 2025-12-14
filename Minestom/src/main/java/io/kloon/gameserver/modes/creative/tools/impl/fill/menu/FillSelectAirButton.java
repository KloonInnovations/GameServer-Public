package io.kloon.gameserver.modes.creative.tools.impl.fill.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.impl.fill.FillTool;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class FillSelectAirButton implements ChestButton {
    private final FillToolMenu menu;

    public FillSelectAirButton(FillToolMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        ItemRef itemRef = menu.getItemRef();
        FillTool.Settings settings = menu.getTool().getItemBound(itemRef);
        CreativePattern pattern = settings.getPattern();
        if (pattern instanceof SingleBlockPattern b && b.getBlock() == Block.AIR) {
            player.sendPit(NamedTextColor.RED, "MOO", MM."<gray>It's already filled with air! Like a balloon!");
            return;
        }

        Block block = Block.AIR;
        boolean edited = menu.getTool().editItemBound(player, itemRef, s -> s.setPattern(new SingleBlockPattern(Block.AIR)));
        if (edited) {
            ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Updated fill block to \{BlockFmt.getName(block)}!",
                    SoundEvent.BLOCK_CHERRY_WOOD_BUTTON_CLICK_OFF, 1);
            player.scheduleNextTick(e -> player.playSound(SoundEvent.ENTITY_COW_HURT, 1.3, 0.3));
            ChestMenuInv.rerender(player);
        }
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;
        Component name = MM."<title>Fill With Air";

        List<Component> lore = ToolDataType.ITEM_BOUND.lore();
        lore.addAll(MM_WRAP."<gray>Testing has shown it is difficult to select this block.");
        lore.add(Component.empty());

        Material icon;
        FillTool.Settings settings = menu.getTool().getItemBound(menu.getItemRef());
        CreativePattern pattern = settings.getPattern();
        if (pattern instanceof SingleBlockPattern b && b.getBlock() == Block.AIR) {
            icon = Material.GRAY_DYE;
            lore.add(MM."<dark_gray>It's already air!");
        } else {
            icon = Material.MILK_BUCKET;
            lore.add(MM."<cta>Click to use air!");
        }

        return MenuStack.of(icon)
                .name(name)
                .lore(lore)
                .build();
    }
}
