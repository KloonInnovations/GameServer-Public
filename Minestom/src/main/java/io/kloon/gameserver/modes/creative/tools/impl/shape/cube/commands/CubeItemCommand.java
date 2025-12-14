package io.kloon.gameserver.modes.creative.tools.impl.shape.cube.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeToolSettings;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.menu.CubeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.sound.SoundEvent;

import java.util.function.BiConsumer;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeTool.*;

public class CubeItemCommand extends ToolItemCommand<CubeTool> {
    public CubeItemCommand(CubeTool tool) {
        super(tool);

        addSyntaxBlockToFillWith();

        ArgumentInteger sizeArg = ArgumentType.Integer("size (blocks)");
        addSyntax(new ToolItemExecutor<>(tool, ToolDataType.ITEM_BOUND) {
            @Override
            public void modifyToolData(CreativePlayer player, CubeToolSettings settings, Preferences pref, CommandContext context) {
                int size = context.get(sizeArg);
                settings.setWidth(size);
                settings.setHeight(size);
                settings.setDepth(size);
                settings.setCuboidButtons(false);
            }

            @Override
            public ToolEditFx createEditFx(CreativePlayer player, CubeToolSettings settings, Preferences pref, CommandContext context) {
                int size = settings.getWidth();
                return new ToolEditFx(
                        MM."<gray>Set cube size to \{size}x\{size}\{size}!",
                        SoundEvent.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON, 0.7);
            }
        }, ArgumentType.Literal("size"), sizeArg);

        addSyntax(new CubeDimensionExecutor(tool, sizeArg, "width", CubeToolSettings::setWidth),
                ArgumentType.Literal("size"), ArgumentType.Literal("x"), sizeArg);
        addSyntax(new CubeDimensionExecutor(tool, sizeArg, "height", CubeToolSettings::setHeight),
                ArgumentType.Literal("size"), ArgumentType.Literal("y"), sizeArg);
        addSyntax(new CubeDimensionExecutor(tool, sizeArg, "depth", CubeToolSettings::setDepth),
                ArgumentType.Literal("size"), ArgumentType.Literal("z"), sizeArg);

        addSyntaxToggleSetting("hollow", CubeToolMenu.HOLLOW);
    }

    private static class CubeDimensionExecutor extends ToolItemExecutor<CubeToolSettings, CubeTool.Preferences> {
        private final ArgumentInteger arg;
        private final String label;
        private final BiConsumer<CubeToolSettings, Integer> set;

        public CubeDimensionExecutor(CreativeTool<CubeToolSettings, Preferences> tool, ArgumentInteger arg, String label, BiConsumer<CubeToolSettings, Integer> set) {
            super(tool, ToolDataType.ITEM_BOUND);
            this.arg = arg;
            this.label = label;
            this.set = set;
        }

        @Override
        public void modifyToolData(CreativePlayer player, CubeToolSettings settings, Preferences pref, CommandContext context) {
            int value = context.get(arg);
            set.accept(settings, value);
        }

        @Override
        public ToolEditFx createEditFx(CreativePlayer player, CubeToolSettings settings, Preferences pref, CommandContext context) {
            int value = context.get(arg);
            return new ToolEditFx(
                    MM."<gray>Set cuboid \{label} to \{value}<gray>!",
                    SoundEvent.BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN, 0.65);
        }
    }
}
