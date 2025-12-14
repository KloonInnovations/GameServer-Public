package io.kloon.gameserver.modes.creative.masks.impl.proximity.command;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.masks.api.MaskItemCommand;
import io.kloon.gameserver.modes.creative.commands.masks.api.MaskItemExecutor;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.ProximityAxis;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.ProximityMask;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;

public class ProximityMaskCommand extends MaskItemCommand<ProximityMask> {
    public ProximityMaskCommand(ProximityMask mask) {
        super(mask);
        ArgumentInteger rangeArg = ArgumentType.Integer("range (manhattan)");
        addSyntax(new MaskItemExecutor<>(mask) {
            @Override
            public void modifyData(CreativePlayer player, ProximityMask.Data data, CommandContext context) {
                int range = context.get(rangeArg);
                data.setRange(range);
            }
        }, rangeArg);

        ArgumentEnum<ProximityAxis> axisArg = ArgumentType.Enum("axis restraint", ProximityAxis.class);
        addSyntax(new MaskItemExecutor<>(mask) {
            @Override
            public void modifyData(CreativePlayer player, ProximityMask.Data data, CommandContext context) {
                int range = context.get(rangeArg);
                data.setRange(range);

                ProximityAxis axis = context.get(axisArg);
                data.setAxis(axis);
            }
        }, rangeArg, axisArg);
    }
}
