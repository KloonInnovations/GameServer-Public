package io.kloon.gameserver.modes.creative.commands.test;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.network.packet.server.play.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WorldBorderTestCommand extends AdminCommand {
    private static final Logger LOG = LoggerFactory.getLogger(WorldBorderTestCommand.class);

    public WorldBorderTestCommand() {
        super("worldbordertest");

        ArgumentDouble xArg = ArgumentType.Double("x");
        ArgumentDouble zArg = ArgumentType.Double("z");
        ArgumentDouble diameterArg = ArgumentType.Double("diameter");
        ArgumentInteger warningBlocksArg = ArgumentType.Integer("warning blocks");
        ArgumentInteger warningSecondsArg = ArgumentType.Integer("warning seconds");
        addSyntax(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                double x = context.get(xArg);
                double z = context.get(zArg);
                double diameter = context.get(diameterArg);
                int warningBlocks = context.get(warningBlocksArg);
                int warningSeconds = context.get(warningSecondsArg);
                player.sendPacket(new WorldBorderCenterPacket(x, z));
                player.sendPacket(new WorldBorderSizePacket(diameter));
                player.sendPacket(new WorldBorderWarningReachPacket(warningBlocks));
                player.sendPacket(new WorldBorderWarningDelayPacket(warningSeconds));
                player.sendMessage(MM."<green>You get the world border!");
            }
        }, xArg, zArg, diameterArg, warningBlocksArg, warningSecondsArg);
    }
}
