package io.kloon.gameserver.modes.creative.tools.impl.copypaste.commands;

import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt.ToolSettingExecutor;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.menu.CopyPasteToolMenu;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.command.builder.arguments.ArgumentType;

import java.util.stream.Stream;

public class CopyPasteItemCommand extends ToolItemCommand<CopyPasteTool> {
    public CopyPasteItemCommand(CopyPasteTool tool) {
        super(tool);

        Stream.of(
                new ItemFlipAxisExecutor(tool, Axis.X),
                new ItemFlipAxisExecutor(tool, Axis.Z)
        ).forEach(exec -> {
            addSyntax(exec, ArgumentType.Literal("flip"), exec.getArg());
        });

        addSyntax(new ItemRotationExecutor(tool, true),
                ArgumentType.Literal("rotate"), ArgumentType.Literal("clockwise"));
        addSyntax(new ItemRotationExecutor(tool, false),
                ArgumentType.Literal("rotate"), ArgumentType.Literal("counter_clockwise"));

        addSyntax(new ToolSettingExecutor<>(tool, CopyPasteToolMenu.IGNORE_PASTING_AIR),
                ArgumentType.Literal("air"));
        addSyntax(new ToolSettingExecutor<>(tool, CopyPasteToolMenu.IGNORE_MASKS),
                ArgumentType.Literal("masks"));
    }
}
