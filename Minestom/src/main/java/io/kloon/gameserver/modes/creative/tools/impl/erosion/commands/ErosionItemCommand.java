package io.kloon.gameserver.modes.creative.tools.impl.erosion.commands;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.ErosionTool;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.ErosionToolSettings;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.menu.ErosionToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.params.ErosionPreset;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.tools.impl.erosion.ErosionTool.*;

public class ErosionItemCommand extends ToolItemCommand<ErosionTool> {
    public ErosionItemCommand(ErosionTool tool) {
        super(tool);

        ArgumentEnum<ErosionPreset> presetArg = ArgumentType.Enum("preset", ErosionPreset.class);
        addSyntax(new ToolItemExecutor<>(tool, ToolDataType.ITEM_BOUND) {
            @Override
            public void modifyToolData(CreativePlayer player, ErosionToolSettings settings, Preferences pref, CommandContext context) {
                ErosionPreset preset = context.get(presetArg);
                settings.setParams(preset.getErosionParams());
            }

            @Override
            public ToolEditFx createEditFx(CreativePlayer player, ErosionToolSettings settings, Preferences pref, CommandContext context) {
                ErosionPreset preset = context.get(presetArg);
                return new ToolEditFx(
                        MM."<gray>Set erosion settings to preset \"\{preset.getName()}\"<gray>!",
                        SoundEvent.ENTITY_ILLUSIONER_MIRROR_MOVE, Pitch.base(1.3 + 0.1 * preset.ordinal())
                );
            }
        });

        addSyntaxNumber("radius", "erosion radius", ErosionToolMenu.RADIUS);
    }
}
