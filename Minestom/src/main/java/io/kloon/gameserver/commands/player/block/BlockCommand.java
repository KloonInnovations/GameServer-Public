package io.kloon.gameserver.commands.player.block;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.commands.executors.TargetAccountVirtualExecutor;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.mongo.blocks.BlockRepo;
import io.kloon.infra.mongo.blocks.PlayerBlock;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class BlockCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(BlockCommand.class);

    public static final String LABEL = "block";

    public BlockCommand() {
        super(LABEL);
        ArgumentString usernameArg = ArgumentType.String("username");
        addSyntax(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                String usernameInput = context.get(usernameArg);
                block(player, usernameInput);
            }
        }, usernameArg);
    }

    public static CompletableFuture<Void> block(KloonPlayer player, String usernameInput) {
        return TargetAccountVirtualExecutor.run(player, usernameInput, targetAccount -> {
            BlockRepo blocks = Kgs.INSTANCE.getBlockRepo();
            boolean alreadyBlocked = blocks.hasBlocked(player.getAccountId(), targetAccount.getId()).join();
            if (alreadyBlocked) {
                player.sendMessage(MM."<red>Mmh... You already blocked that player!");
                player.sendMessage(MM."<red>Use /\{UnblockCommand.LABEL} to remove it.");
                return;
            }

            if (targetAccount.getId().equals(player.getAccountId())) {
                player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 1);
                player.sendPit(NamedTextColor.RED, "WHAT?", MM."<gray>Can't /block yourself, even if you really want to.");
                return;
            }

            if (targetAccount.ranks().getStaffRank().isStaff()) {
                player.playSound(SoundEvent.ENTITY_WOLF_WHINE, 0.9);
                player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>You may not /block staff members!");
                return;
            }

            PlayerBlock newBlock = new PlayerBlock(player.getAccountId(), targetAccount.getId());
            blocks.add(newBlock).join();
            Kgs.getCaches().playerBlocks().invalidate(player.getAccountId(), targetAccount.getId());
            player.sendPit(NamedTextColor.RED, "BLOCKED!", MM."<gray>You blocked \{targetAccount.getDisplayMM()}<gray>!");
            player.playSound(SoundEvent.ENTITY_WITHER_SPAWN, Pitch.base(1.65).addRand(0.2));
        });
    }
}
