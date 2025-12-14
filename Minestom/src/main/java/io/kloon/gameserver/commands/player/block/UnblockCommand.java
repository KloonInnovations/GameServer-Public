package io.kloon.gameserver.commands.player.block;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.commands.executors.TargetAccountVirtualExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.mongo.blocks.BlockRepo;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class UnblockCommand extends Command {
    public static final String LABEL = "unblock";

    public UnblockCommand() {
        super(LABEL);
        ArgumentString usernameArg = ArgumentType.String("username");
        addSyntax(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                String usernameInput = context.get(usernameArg);
                unblock(player, usernameInput);
            }
        }, usernameArg);
    }

    public static CompletableFuture<Void> unblock(KloonPlayer player, String usernameInput) {
        return TargetAccountVirtualExecutor.run(player, usernameInput, targetAccount -> {
            BlockRepo blocks = Kgs.INSTANCE.getBlockRepo();
            boolean isBlocked = blocks.hasBlocked(player.getAccountId(), targetAccount.getId()).join();
            if (!isBlocked) {
                player.sendMessage(MM."<red>This player isn't blocked!");
                return;
            }

            blocks.remove(player.getAccountId(), targetAccount.getId()).join();
            Kgs.getCaches().playerBlocks().invalidate(player.getAccountId(), targetAccount.getId());
            player.sendPit(NamedTextColor.GREEN, "UNBLOCKED!", MM."<gray>You unblocked \{targetAccount.getDisplayMM()}<gray>!");
            player.playSound(SoundEvent.ENTITY_PLAYER_LEVELUP, 1.8);
        });
    }
}
