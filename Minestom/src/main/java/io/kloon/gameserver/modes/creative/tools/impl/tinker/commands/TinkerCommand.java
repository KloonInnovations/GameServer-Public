package io.kloon.gameserver.modes.creative.tools.impl.tinker.commands;

import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.CommandWithUsage;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class TinkerCommand extends Command implements CommandWithUsage {
    public static final String LABEL = "tinker";
    public static final String ALT_LABEL = "item";

    public TinkerCommand() {
        super(LABEL, ALT_LABEL);

        ArgumentBlockState blockStateArg = ArgumentType.BlockState("block");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                Block block = context.get(blockStateArg);
                TinkeredBlock tinkered = new TinkeredBlock(block);
                ItemStack item = tinkered.toItem();
                player.getInventoryExtras().grab(item);

                player.msg().send(MsgCat.INVENTORY,
                        NamedTextColor.GREEN, "ITEM!", MM."<gray>Picked up \{tinkered.getNameMM()}<gray>!",
                        SoundEvent.ENTITY_ITEM_PICKUP, 0.8);
            }
        }, blockStateArg);
    }

    @Override
    public List<ToolOperationUsage> getUsages() {
        return Arrays.asList(
                new ToolOperationUsage("<block>", "Gives you an item for any block.")
        );
    }
}
