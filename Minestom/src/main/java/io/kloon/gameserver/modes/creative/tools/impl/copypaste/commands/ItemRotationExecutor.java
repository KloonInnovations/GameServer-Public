package io.kloon.gameserver.modes.creative.tools.impl.copypaste.commands;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteSettings;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipRotation;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ItemRotationExecutor extends ToolItemExecutor<CopyPasteSettings, CopyPasteTool.Preferences> {
    private final boolean clockwise;

    public ItemRotationExecutor(CreativeTool<CopyPasteSettings, CopyPasteTool.Preferences> tool, boolean clockwise) {
        super(tool, ToolDataType.ITEM_BOUND);
        this.clockwise = clockwise;
    }

    @Override
    public void modifyToolData(CreativePlayer player, CopyPasteSettings settings, CopyPasteTool.Preferences pref, CommandContext context) {
        ClipRotation rotation = settings.getRotation();
        rotation = clockwise ? rotation.clockwise() : rotation.counterwise();
        settings.setRotation(rotation);
    }

    @Override
    public ToolEditFx createEditFx(CreativePlayer player, CopyPasteSettings settings, CopyPasteTool.Preferences pref, CommandContext context) {
        ClipRotation rotation = settings.getRotation();
        Component msg = clockwise
                ? MM."<gray>Rotated 90 degrees clockwise, now at \{rotation.label().toLowerCase()}!"
                : MM."<gray>Rotated 90 degrees counter-clockwise, now at \{rotation.label().toLowerCase()}!";
        return new ToolEditFx(msg, SoundEvent.ENTITY_BREEZE_SLIDE, Pitch.rng(1.5, 0.2));
    }
}
