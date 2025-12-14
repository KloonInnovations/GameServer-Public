package io.kloon.gameserver.commands.executors;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.mongo.accounts.KloonAccount;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class TargetAccountVirtualExecutor extends VirtualExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(TargetAccountVirtualExecutor.class);

    private final ArgumentString usernameOrUuidArg;

    public TargetAccountVirtualExecutor(ArgumentString usernameOrUuidArg) {
        this.usernameOrUuidArg = usernameOrUuidArg;
    }

    @Override
    public final void apply(@NotNull KloonPlayer sender, @NotNull CommandContext context) {
        String usernameOrUuid = context.get(usernameOrUuidArg);

        KloonAccount account;
        try {
            UUID uuid;
            if (usernameOrUuid.contains("-")) {
                try {
                    uuid = UUID.fromString(usernameOrUuid);
                } catch (Throwable t) {
                    sender.sendMessage(MM."<red>Couldn't parse uuid!");
                    return;
                }
            } else {
                uuid = Kgs.getInfra().caches().uuids().getByUsername(usernameOrUuid).join();
                if (uuid == null) {
                    sender.sendMessage(MM."<red>Couldn't find uuid for that username!");
                    return;
                }
            }

            account = Kgs.getAccountsRepo().get(uuid).join();
        } catch (Throwable t) {
            LOG.error("Error getting target account", t);
            sender.sendMessage(MM."<red>Error getting target account from username!");
            return;
        }

        if (account == null) {
            sender.sendMessage(MM."<red>Couldn't find that player! Sorry!");
            return;
        }

        apply(sender, context, account);
    }

    public static CompletableFuture<Void> run(KloonPlayer sender, String usernameInput, Consumer<KloonAccount> consumer) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Thread.ofVirtual().start(() -> {
            try {
                UUID uuid = Kgs.getInfra().caches().uuids().getByUsername(usernameInput).join();
                KloonAccount targetAccount = uuid == null ? null : Kgs.getAccountsRepo().get(uuid).join();
                if (uuid == null || targetAccount == null) {
                    sender.sendPit(NamedTextColor.RED, "WHO?", MM."<gray>Couldn't find a player with username \"\{usernameInput}\"!");
                    return;
                }

                consumer.accept(targetAccount);
                future.complete(null);
            } catch (Throwable t) {
                LOG.error("Error running virtual something", t);
                sender.sendPit(NamedTextColor.DARK_RED, "ERROR!", MM."<gray>While processing this command!");
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public abstract void apply(@NotNull KloonPlayer sender, @NotNull CommandContext context, KloonAccount target);
}
