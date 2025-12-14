package io.kloon.gameserver.modes.creative.tools.impl.laser.commands;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt.ToolNumberExecutor;
import io.kloon.gameserver.modes.creative.tools.impl.laser.LaserTool;
import io.kloon.gameserver.modes.creative.tools.impl.laser.LaserToolSettings;
import io.kloon.gameserver.modes.creative.tools.impl.laser.menu.LaserToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.LaserModeType;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.tools.impl.laser.LaserTool.Preferences;

public class LaserItemCommand extends ToolItemCommand<LaserTool> {
    public LaserItemCommand(LaserTool tool) {
        super(tool);

        addSyntaxBlockToFillWith();

        ArgumentEnum<LaserModeType> modeArg = ArgumentType.Enum("laser mode", LaserModeType.class);
        addSyntax(new ToolItemExecutor<>(tool, ToolDataType.ITEM_BOUND) {
            @Override
            public void modifyToolData(CreativePlayer player, LaserToolSettings settings, Preferences pref, CommandContext context) {
                LaserModeType laserMode = context.get(modeArg);
                settings.setMode(laserMode);
            }

            @Override
            public ToolEditFx createEditFx(CreativePlayer player, LaserToolSettings settings, Preferences pref, CommandContext context) {
                LaserModeType mode = settings.getMode();
                return new ToolEditFx(
                        MM."<gray>Switched mode to \{mode.label()}<gray>!",
                        SoundEvent.ENTITY_MOOSHROOM_CONVERT, Pitch.rng(1.85, 0.15));
            }
        }, ArgumentType.Literal("mode"), modeArg);

        ArgumentDouble radiusArg = ArgumentType.Double("blocks radius");
        addSyntax(new ToolNumberExecutor<>(tool, LaserToolMenu.LASER_RADIUS, radiusArg),
                ArgumentType.Literal("radius"), radiusArg);

        ArgumentDouble offsetArg = ArgumentType.Double("blocks in front of you");
        addSyntax(new ToolNumberExecutor<>(tool, LaserToolMenu.LASER_OFFSET, offsetArg),
                ArgumentType.Literal("offset"), offsetArg);
    }
}
