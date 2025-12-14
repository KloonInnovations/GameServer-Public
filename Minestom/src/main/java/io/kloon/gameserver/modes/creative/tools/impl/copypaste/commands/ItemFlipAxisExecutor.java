package io.kloon.gameserver.modes.creative.tools.impl.copypaste.commands;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteSettings;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.util.coordinates.Axis;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

class ItemFlipAxisExecutor extends ToolItemExecutor<CopyPasteSettings, CopyPasteTool.Preferences> {
    private final Axis axis;

    public ItemFlipAxisExecutor(CreativeTool<CopyPasteSettings, CopyPasteTool.Preferences> tool, Axis axis) {
        super(tool, ToolDataType.ITEM_BOUND);
        this.axis = axis;
    }

    public ArgumentLiteral getArg() {
        return ArgumentType.Literal(axis.name().toLowerCase());
    }

    @Override
    public void modifyToolData(CreativePlayer player, CopyPasteSettings settings, CopyPasteTool.Preferences pref, CommandContext context) {
        boolean flipped = switch (axis) {
            case X -> settings.isFlipX();
            case Z -> settings.isFlipZ();
            default -> throw new IllegalStateException();
        };

        flipped = !flipped;
        switch (axis) {
            case X -> settings.setFlipX(flipped);
            case Z -> settings.setFlipZ(flipped);
        }
    }

    @Override
    public ToolEditFx createEditFx(CreativePlayer player, CopyPasteSettings settings, CopyPasteTool.Preferences pref, CommandContext context) {
        boolean flipped = settings.isFlipX();
        Component details = MM."<gray>\{flipped ? "Flipped" : "Unflipped"} on the \{axis.name()} axis!";
        return new ToolEditFx(details, SoundEvent.ENTITY_BREEZE_JUMP, Pitch.rng(1.6, 0.2));
    }
}
