package io.kloon.gameserver.commands.admin;

import io.kloon.bigbackend.events.InvalidateMonikerEvent;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.commands.executors.VirtualExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.mongo.accounts.KloonAccount;
import io.kloon.infra.ranks.StaffRank;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class StaffRankCommands extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(StaffRankCommands.class);

    public StaffRankCommands() {
        super("staffrank");
        addSubcommand(new SetPlayerRankCommand());
    }

    public static class SetPlayerRankCommand extends Command {
        public SetPlayerRankCommand() {
            super("set");

            ArgumentString usernameArg = ArgumentType.String("username");
            ArgumentEnum<StaffRank> rankArg = ArgumentType.Enum("rank", StaffRank.class);
            addSyntax(new VirtualExecutor() {
                @Override
                public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                    String username = context.get(usernameArg);
                    StaffRank playerRank = context.get(rankArg);

                    try {
                        UUID uuid = Kgs.getInfra().caches().uuids().getByUsername(username).join();
                        if (uuid == null) {
                            player.sendMessage(MM."<red>Couldn't find uuid for that username!");
                            return;
                        }
                        KloonAccount account = Kgs.getAccountsRepo().get(uuid).join();
                        Kgs.getAccountsRepo().setPlayerRank(account.getId(), playerRank.getDbKey()).join();
                        Kgs.INSTANCE.getDiscord().pushMetadataIfLinked(account);
                        Kgs.getBackend().getInvalidation().broadcastMonikerInvalidate(new InvalidateMonikerEvent(account.getId(), account.getMinecraftUuid()));
                        player.sendMessage(MM."<green>Updated \{account.moniker().getDisplayMM()} <green>to \{playerRank}!");
                    } catch (Throwable t) {
                        LOG.error("Error updating player rank", t);
                        player.sendMessage(MM."<red>Error updating the player's rank!");
                    }
                }
            }, usernameArg, rankArg);
        }
    }
}
