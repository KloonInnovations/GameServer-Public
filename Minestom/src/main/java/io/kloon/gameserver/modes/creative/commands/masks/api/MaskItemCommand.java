package io.kloon.gameserver.modes.creative.commands.masks.api;

import io.kloon.gameserver.modes.creative.masks.MaskType;
import net.minestom.server.command.builder.Command;

public class MaskItemCommand<T extends MaskType> extends Command {
    protected final T mask;

    public MaskItemCommand(T mask) {
        super(mask.getCommandLabel());
        this.mask = mask;
    }
}
