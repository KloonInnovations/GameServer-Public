package io.kloon.gameserver.modes.creative.tools.menus.commands;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.snipe.SnipeCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.CommandWithUsage;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToolCommandsInfo implements ChestButton {
    private final CreativeTool<?, ?> tool;

    public static final ToolOperationUsage SNIPE_USAGE = new ToolOperationUsage("", "Edit snipe settings.");

    public ToolCommandsInfo(CreativeTool<?, ?> tool) {
        this.tool = tool;
    }

    public ToolCommandsInfo(CreativeToolMenu<?> toolMenu) {
        this.tool = toolMenu.getTool();
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<title>Tool Commands";

        Lore lore = new Lore();
        lore.add(MM."<dark_gray>\{tool.getType().getDisplayName()}");
        lore.addEmpty();

        List<Command> commands = tool.createCommands();
        List<Lore> usages = commands.stream()
                .flatMap(c -> {
                    String commandName = c.getName();
                    if (c instanceof CommandWithUsage usageCmd) {
                        return usageCmd.getUsages().stream()
                                .map(usage -> usage.lore(commandName));
                    }
                    if (c instanceof ToolItemCommand<?> itemCommand) {
                        return Stream.of(itemCommand.usagelore(commandName));
                    }
                    return null;
                }).filter(Objects::nonNull).toList();
        if (usages.isEmpty()) {
            lore.wrap("<gray>This tool doesn't have any associated commands.");
        } else {
            for (int i = 0; i < usages.size(); ++i) {
                Lore usage = usages.get(i);
                lore.add(usage);
                if (i !=  usages.size() - 1) {
                    lore.addEmpty();
                }
            }
        }

        if (tool.usesQSnipe(player)) {
            lore.addEmpty();
            lore.add(SNIPE_USAGE.lore(SnipeCommand.LABEL));
        }

        return MenuStack.of(Material.COMMAND_BLOCK, name, lore);
    }
}
