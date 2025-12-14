package io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToolSettingExecutor<ItemBound, PlayerBound> extends ToolItemExecutor<ItemBound, PlayerBound> {
    private final ToolToggle<ItemBound> toggle;

    public ToolSettingExecutor(CreativeTool<ItemBound, PlayerBound> tool, ToolToggle<ItemBound> toggle) {
        super(tool, ToolDataType.ITEM_BOUND);
        this.toggle = toggle;
    }

    @Override
    public void modifyToolData(CreativePlayer player, ItemBound settings, PlayerBound pref, CommandContext context) {
        boolean enabled = toggle.isEnabled().apply(settings);
        enabled = !enabled;
        toggle.editEnabled().apply(settings, enabled);
    }

    @Override
    public ToolEditFx createEditFx(CreativePlayer player, ItemBound settings, PlayerBound pref, CommandContext context) {
        boolean enabled = toggle.isEnabled().apply(settings);
        double pitch = enabled ? 1.9 : 1.5;

        Component msg = enabled
                ? MM."<gray>Toggled \{toggle.name()} to <green>ON<gray>!"
                : MM."<gray>Toggled \{toggle.name()} to <red>OFF<gray>!";

        return new ToolEditFx(msg, SoundEvent.BLOCK_NOTE_BLOCK_PLING, Pitch.base(pitch));
    }
}
