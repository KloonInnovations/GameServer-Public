package io.kloon.gameserver.modes.creative.commands.test;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.util.SwirlIteration;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class TestSwirlCommand extends AdminCommand {
    public TestSwirlCommand() {
        super("testswirl");
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                BlockVec center = player.getTargetBlockPositionOrMax(10);
                AtomicInteger counter = new AtomicInteger(0);
                SwirlIteration.iterate(center, 4, Axis.Z, blockPos -> {
                    int tick = counter.incrementAndGet();
                    player.scheduleTicks(() -> {
                        player.getInstance().setBlock(blockPos, Block.STONE);
                    }, tick + 1);
                    return true;
                });
                player.sendMessage("Swirl " + counter.get());
            }
        });
    }
}
