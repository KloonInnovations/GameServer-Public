package io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.patterns.ArgumentPattern;
import io.kloon.gameserver.modes.creative.commands.patterns.PatternParseException;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.data.ItemBoundPattern;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToolBlockExecutor<ItemBound extends ItemBoundPattern, PlayerBound> extends ToolItemExecutor<ItemBound, PlayerBound> {
    private final ArgumentPattern patternArg;

    public ToolBlockExecutor(CreativeTool<ItemBound, PlayerBound> tool, ArgumentPattern blockArg) {
        super(tool, ToolDataType.ITEM_BOUND);
        this.patternArg = blockArg;
    }

    @Override
    public void modifyToolData(CreativePlayer player, ItemBound settings, PlayerBound pref, CommandContext context) throws PatternParseException {
        CreativePattern pattern = context.get(patternArg);
        settings.setPattern(pattern);
    }

    @Override
    public ToolEditFx createEditFx(CreativePlayer player, ItemBound settings, PlayerBound pref, CommandContext context) {
        CreativePattern pattern = settings.getPattern();
        Component msg = pattern instanceof SingleBlockPattern
                ? MM."<gray>Set fill block to \{pattern.labelMM()}!"
                : MM."<gray>Set fill pattern to \{pattern.labelMM()}!";
        return new ToolEditFx(msg, SoundEvent.ENTITY_AXOLOTL_SPLASH, Pitch.base(0.7));
    }
}
