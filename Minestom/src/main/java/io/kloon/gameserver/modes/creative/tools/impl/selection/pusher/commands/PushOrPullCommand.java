package io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.SelectionPusherTool;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PushOrPullCommand extends ToolOperationCommand<SelectionPusherTool> {
    private final boolean pushing;

    public PushOrPullCommand(SelectionPusherTool tool, String name, boolean pushing) {
        super(tool, name);
        this.pushing = pushing;

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CardinalDirection dir = CardinalDirection.closestDir(player.getLookVec());
                tool.pushSelection(player, dir, 1, pushing);
            }
        });

        ArgumentInteger blocksArg = ArgumentType.Integer("blocks");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CardinalDirection dir = CardinalDirection.closestDir(player.getLookVec());
                int distance = context.get(blocksArg);
                tool.pushSelection(player, dir, distance, pushing);
            }
        }, blocksArg);

        if (pushing) {
            ArgumentEnum<CardinalDirection> directionArg = ArgumentType.Enum("direction", CardinalDirection.class);
            addSyntax(new CreativeExecutor() {
                @Override
                public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                    CardinalDirection dir = context.get(directionArg);
                    tool.pushSelection(player, dir, 1, true);
                }
            }, directionArg);
            addSyntax(new CreativeExecutor() {
                @Override
                public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                    CardinalDirection dir = context.get(directionArg);
                    int distance = context.get(blocksArg);
                    tool.pushSelection(player, dir, distance, true);
                }
            }, directionArg, blocksArg);

            ArgumentInteger xArg = ArgumentType.Integer("x");
            ArgumentInteger yArg = ArgumentType.Integer("y");
            ArgumentInteger zArg = ArgumentType.Integer("z");
            addSyntax(new CreativeExecutor() {
                @Override
                public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                    Vec moving = new Vec(
                            context.get(xArg),
                            context.get(yArg),
                            context.get(zArg));
                    tool.moveSelection(player, moving);
                }
            }, xArg, yArg, zArg);
        }
    }

    @Override
    public List<ToolOperationUsage> getUsages() {
        List<ToolOperationUsage> usages = new ArrayList<>();
        usages.add(new ToolOperationUsage("[blocks]",
                pushing ? "Pushes <selection> <gray>in the direction you're facing." : "Pulls your <selection> <gray>against the direction you're facing."));

        if (pushing) {
            usages.add(new ToolOperationUsage("<direction> [blocks}",
                    "Pushes <selection> <gray>in specified direction."));

            usages.add(new ToolOperationUsage("<x> <y> <z>",
                    "Moves <selection> <gray>directionally."));
        }

        return usages;
    }
}
