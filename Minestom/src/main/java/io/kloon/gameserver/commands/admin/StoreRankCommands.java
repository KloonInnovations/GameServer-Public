package io.kloon.gameserver.commands.admin;

import io.kloon.bigbackend.events.InvalidateMonikerEvent;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.commands.executors.TargetAccountVirtualExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.mongo.accounts.KloonAccount;
import io.kloon.infra.ranks.StoreRank;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class StoreRankCommands extends Command {
    public StoreRankCommands() {
        super("storerank");
        addSubcommand(new AddStoreRankCommand());
        addSubcommand(new RemoveStoreRankCommand());
    }

    public static class AddStoreRankCommand extends Command {
        public AddStoreRankCommand() {
            super("add");

            ArgumentString usernameArg = ArgumentType.String("username");
            ArgumentEnum<StoreRank> rankArg = ArgumentType.Enum("rank", StoreRank.class);
            addSyntax(new TargetAccountVirtualExecutor(usernameArg) {
                @Override
                public void apply(@NotNull KloonPlayer sender, @NotNull CommandContext context, KloonAccount target) {
                    StoreRank storeRank = context.get(rankArg);
                    if (storeRank == StoreRank.NONE || storeRank == StoreRank.PRO) {
                        sender.sendMessage(MM."<red>You can't actually add that rank!");
                        return;
                    }

                    Kgs.getAccountsRepo().addStoreRank(target.getId(), storeRank.getDbKey()).join();
                    Kgs.INSTANCE.getDiscord().pushMetadataIfLinked(target);
                    Kgs.getBackend().getInvalidation().broadcastMonikerInvalidate(new InvalidateMonikerEvent(target.getId(), target.getMinecraftUuid()));
                    sender.sendMessage(MM."<green>Added \{storeRank.name()} to \{target.getDisplayMM()}<green>!");
                }
            }, usernameArg, rankArg);
        }
    }

    public static class RemoveStoreRankCommand extends Command {
        public RemoveStoreRankCommand() {
            super("remove");
            ArgumentString usernameArg = ArgumentType.String("username");
            ArgumentEnum<StoreRank> rankArg = ArgumentType.Enum("rank", StoreRank.class);
            addSyntax(new TargetAccountVirtualExecutor(usernameArg) {
                @Override
                public void apply(@NotNull KloonPlayer sender, @NotNull CommandContext context, KloonAccount target) {
                    StoreRank storeRank = context.get(rankArg);
                    Kgs.getAccountsRepo().removeStoreRank(target.getId(), storeRank.getDbKey()).join();
                    Kgs.INSTANCE.getDiscord().pushMetadataIfLinked(target);
                    sender.sendMessage(MM."<green>Removed \{storeRank.name()} from \{target.getDisplayMM()}<green>!");
                }
            }, usernameArg, rankArg);
        }
    }
}
