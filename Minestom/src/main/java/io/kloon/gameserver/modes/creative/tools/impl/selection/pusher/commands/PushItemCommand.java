package io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.commands;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.SelectionPusherTool;
import io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.menu.SelectionPushDistanceButton;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.SelectionPusherTool.*;

public class PushItemCommand extends ToolItemCommand<SelectionPusherTool> {
    public PushItemCommand(SelectionPusherTool tool) {
        super(tool);

        ArgumentNumber<Integer> rangeArg = ArgumentType.Integer("distance (blocks)")
                .min(SelectionPushDistanceButton.MIN)
                .max(SelectionPushDistanceButton.MAX);
        addSyntax(new ToolItemExecutor<>(tool, ToolDataType.ITEM_BOUND) {
            @Override
            public void modifyToolData(CreativePlayer player, Settings settings, Preferences pref, CommandContext context) {
                settings.blocks = context.get(rangeArg);
            }

            @Override
            public ToolEditFx createEditFx(CreativePlayer player, Settings settings, Preferences pref, CommandContext context) {
                return new ToolEditFx(
                        MM."<gray>Updated number of blocks to <green>\{NumberFmt.NO_DECIMAL.format(settings.blocks)}<gray>!",
                        SoundEvent.BLOCK_NOTE_BLOCK_PLING, Pitch.range(settings.blocks, 1, 32)
                );
            }
        }, ArgumentType.Literal("distance"), rangeArg);
    }
}
