package io.kloon.gameserver.modes.creative.menu.tools;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.ToolCommand;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.ToolsListener;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class ToolsSelectionMenu extends ChestMenu {
    public static final String ICON = "\uD83D\uDD27"; // 🔧

    private final CreativePlayer player;
    private final ChestMenu parent;
    private final ToolsListener tools;

    public ToolsSelectionMenu(CreativePlayer player, ChestMenu parent, ToolsListener tools) {
        super(STR."\{ICON} Tools Selection");
        this.player = player;
        this.parent = parent;
        this.tools = tools;
    }

    @Override
    protected void registerButtons() {
        List<CreativeTool> toolsList = new ArrayList<>(availableTools().toList());
        toolsList.sort(Comparator.comparingInt(t -> t.getType().ordinal()));

        ChestLayouts.INSIDE.distribute(toolsList, (slot, tool) -> {
            reg(slot, new ToolPickupButton(tool));
        });

        reg().goBack(parent);
        reg(size.last(), new ToolSelectionCommandsButton());
    }

    private Stream<CreativeTool> availableTools() {
        if (!player.canEditWorld()) {
            return tools.getAll().stream()
                    .filter(tool -> tool.getType().isAvailableWithoutBuildPerms());

        }
        return tools.getAll().stream();
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;
        Component name = MM."<green>\{ICON} <title>Tools Selection";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<cmd>\{ToolCommand.TOOLS_ALT}");
        lore.add(Component.empty());

        if (player.canEditWorld()) {
            lore.addAll(MM_WRAP."<gray>Pick from a wide array of tools to help you build.");
        } else {
            lore.addAll(MM_WRAP."<gray>Pick movement tools and other utilities to visit this build.");
        }
        lore.add(Component.empty());

        lore.add(MM."<cta>Click to browse!");

        return MenuStack.of(Material.CHEST)
                .name(name)
                .lore(lore)
                .build();
    }
}
