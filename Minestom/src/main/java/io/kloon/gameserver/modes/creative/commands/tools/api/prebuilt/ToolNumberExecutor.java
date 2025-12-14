package io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToolNumberExecutor<ItemBound, PlayerBound> extends ToolItemExecutor<ItemBound, PlayerBound> {
    private final NumberInput<ItemBound> number;
    private final ArgumentNumber<? extends Number> arg;

    public ToolNumberExecutor(CreativeTool<ItemBound, PlayerBound> tool, NumberInput<ItemBound> number, ArgumentNumber<? extends Number> arg) {
        super(tool, ToolDataType.ITEM_BOUND);
        this.number = number;
        this.arg = arg;
    }

    @Override
    public void modifyToolData(CreativePlayer player, ItemBound settings, PlayerBound pref, CommandContext context) {
        Number input = context.get(arg);
        double value = Math.clamp(input.doubleValue(), number.min(), number.max());
        number.editValue().apply(settings, value);
    }

    @Override
    public ToolEditFx createEditFx(CreativePlayer player, ItemBound settings, PlayerBound pref, CommandContext context) {
        double value = number.getValue().apply(settings);
        Component msg = MM."<gray>Adjusted \{number.name().toLowerCase()} to <green>\{NumberFmt.NO_DECIMAL.format(value)}!";
        return new ToolEditFx(msg, SoundEvent.BLOCK_NOTE_BLOCK_BELL, Pitch.rng(1.1, 0.1));
    }
}
