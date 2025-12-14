package io.kloon.gameserver.modes.creative.commands.tools;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.tools.ToolsSelectionMenu;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolsListener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToolCommand extends Command {
    public static final String LABEL = "tool";
    public static final String SHORTHAND = "t";
    public static final String TOOLS_ALT = "tools";

    public ToolCommand() {
        super(LABEL, SHORTHAND, TOOLS_ALT);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                ToolsListener toolsListener = player.getCreative().getToolsListener();

                CreativeMainMenu mainMenu = new CreativeMainMenu(player);
                new ToolsSelectionMenu(player, mainMenu, toolsListener).display(player);
            }
        });

        for (CreativeToolType toolType : CreativeToolType.values()) {
            addSyntax(new CreativeExecutor() {
                @Override
                public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                    boolean canEdit = player.canEditWorld();

                    ToolsListener toolsListener = player.getCreative().getToolsListener();
                    if (!toolType.canInstantiate()) {
                        player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>You cannot spawn this tool using this command!");
                        return;
                    }

                    if (!canEdit && !toolType.isAvailableWithoutBuildPerms()) {
                        player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>This tool is unavailable without edit permissions!");
                        return;
                    }

                    CreativeTool tool = toolsListener.get(toolType);
                    tool.giveToPlayer(player);
                }

                @Override
                public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                    return true;
                }
            }, ArgumentType.Literal(toolType.getDbKey()));
        }
    }
}
