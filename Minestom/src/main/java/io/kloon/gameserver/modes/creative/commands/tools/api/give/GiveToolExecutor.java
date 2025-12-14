package io.kloon.gameserver.modes.creative.commands.tools.api.give;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class GiveToolExecutor extends CreativeExecutor {
    private final CreativeTool<?, ?> tool;

    public GiveToolExecutor(CreativeTool<?, ?> tool) {
        this.tool = tool;
    }

    @Override
    public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
        boolean canEdit = player.canEditWorld();

        CreativeToolType toolType = tool.getType();
        if (!toolType.canInstantiate()) {
            player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>You cannot spawn this tool using this command!");
            return;
        }

        if (!canEdit && !toolType.isAvailableWithoutBuildPerms()) {
            player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>This tool is unavailable without edit permissions!");
            return;
        }

        tool.giveToPlayer(player);
    }

    @Override
    public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
        return true;
    }
}
