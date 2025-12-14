package io.kloon.gameserver.modes.creative.menu.tools;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.ToolCommand;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToolPickupButton implements ChestButton {
    private final CreativeTool tool;

    public ToolPickupButton(CreativeTool<?, ?> tool) {
        this.tool = tool;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        CreativePlayer cp = (CreativePlayer) player;
        tool.giveToPlayer(cp);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        CreativeToolType toolType = tool.getType();
        Component name = MM."\{toolType.getDisplayName()}";

        List<Component> lore = new ArrayList<>();
        lore.add(renderCommand(toolType));
        lore.add(toolType.getCategory().getLoreLine());
        lore.add(Component.empty());
        tool.writeUsage(lore, tool.createDefaultItemBound(), tool.getPlayerBound(player));
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to pickup tool!");

        return new ItemBuilder2(toolType.getMaterial())
                .hideFlags()
                .name(name)
                .lore(lore)
                .build();
    }

    private Component renderCommand(CreativeToolType toolType) {
        String override = toolType.getCommandOverride();
        if (override == null) {
            return MM."<dark_gray>/\{ToolCommand.SHORTHAND} \{ toolType.getDbKey()}";
        }
        return MM."<dark_gray>/\{override}";
    }
}
