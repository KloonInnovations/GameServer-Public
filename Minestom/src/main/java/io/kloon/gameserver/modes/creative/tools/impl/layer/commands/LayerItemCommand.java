package io.kloon.gameserver.modes.creative.tools.impl.layer.commands;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt.ToolNumberExecutor;
import io.kloon.gameserver.modes.creative.tools.impl.layer.LayerTool;
import io.kloon.gameserver.modes.creative.tools.impl.layer.menu.LayerToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.layer.params.LayerAxis;
import io.kloon.gameserver.modes.creative.tools.impl.layer.params.LayerShape;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.tools.impl.layer.LayerTool.Preferences;
import static io.kloon.gameserver.modes.creative.tools.impl.layer.LayerTool.Settings;

public class LayerItemCommand extends ToolItemCommand<LayerTool> {
    public LayerItemCommand(LayerTool tool) {
        super(tool);

        addSyntaxBlockToFillWith();

        ArgumentDouble radiusArg = ArgumentType.Double("radius (blocks)");
        addSyntax(new ToolNumberExecutor<>(tool, LayerToolMenu.RADIUS, radiusArg),
                ArgumentType.Literal("radius"), radiusArg);

        ArgumentEnum<LayerAxis> axisArg = ArgumentType.Enum("axis arg", LayerAxis.class);
        addSyntax(new ToolItemExecutor<>(tool, ToolDataType.ITEM_BOUND) {
            @Override
            public void modifyToolData(CreativePlayer player, Settings settings, Preferences pref, CommandContext context) {
                LayerAxis layerAxis = context.get(axisArg);
                settings.setAxis(layerAxis);
            }

            @Override
            public ToolEditFx createEditFx(CreativePlayer player, Settings settings, Preferences pref, CommandContext context) {
                LayerAxis axis = settings.getAxis();
                return new ToolEditFx(
                        MM."<gray>Set layer axis to \{axis.label()}<gray>!",
                        SoundEvent.BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE, Pitch.base(0.65 + axis.ordinal() * 0.05)
                );
            }
        }, ArgumentType.Literal("axis"), axisArg);

        ArgumentEnum<LayerShape> shapeArg = ArgumentType.Enum("shape arg", LayerShape.class);
        addSyntax(new ToolItemExecutor<>(tool, ToolDataType.ITEM_BOUND) {
            @Override
            public void modifyToolData(CreativePlayer player, Settings settings, Preferences pref, CommandContext context) {
                LayerShape layerShape = context.get(shapeArg);
                settings.setShape(layerShape);
            }

            @Override
            public ToolEditFx createEditFx(CreativePlayer player, Settings settings, Preferences pref, CommandContext context) {
                LayerShape shape = settings.getShape();
                return new ToolEditFx(
                        MM."<gray>Set layer shape to \{shape.label()}<gray>!",
                        SoundEvent.ENTITY_MOOSHROOM_CONVERT, 1.8 + shape.ordinal() * 0.1
                );
            }
        }, ArgumentType.Literal("shape"), shapeArg);
    }
}
