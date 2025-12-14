package io.kloon.gameserver.commands.moderation;

import com.google.common.collect.Sets;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.commands.conditions.StaffRankCondition;
import io.kloon.gameserver.commands.executors.TargetAccountVirtualExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.input.DurationInput;
import io.kloon.infra.mongo.accounts.KloonAccount;
import io.kloon.infra.mongo.bans.MinecraftBan;
import io.kloon.infra.mongo.bans.MinecraftBanRepo;
import io.kloon.infra.ranks.StaffRank;
import io.kloon.velocity.client.ProxyApiClient;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import static io.kloon.infra.adventure.MiniMessageTemplate.MM;

public class BanCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(BanCommand.class);

    public BanCommand() {
        super("ban");
        setCondition(new StaffRankCondition(StaffRank.MODERATOR));

        ArgumentString identifierArg = ArgumentType.String("username or uuid");
        ArgumentString durationArg = ArgumentType.String("duration");
        durationArg.setSuggestionCallback((sender, context, suggestion) -> {
            suggestion.addEntry(new SuggestionEntry("2h"));
            suggestion.addEntry(new SuggestionEntry("1d"));
            suggestion.addEntry(new SuggestionEntry("perm"));
        });
        addSyntax(new TargetAccountVirtualExecutor(identifierArg) {
            @Override
            public void apply(@NotNull KloonPlayer sender, @NotNull CommandContext context, KloonAccount target) {
                String durationInput = context.get(durationArg);
                long expiryMs;
                try {
                    expiryMs = parseExpiry(durationInput);
                } catch (Throwable t) {
                    sender.sendMessage(MM."<red>Failed parsing duration!");
                    LOG.warn("Failed parsing duration from ban command", t);
                    return;
                }

                MinecraftBanRepo bans = new MinecraftBanRepo(Kgs.getInfra().mongo());
                ProxyApiClient proxyApiClient = new ProxyApiClient(Kgs.getInfra().nats());

                UUID minecraftUuid = target.getMinecraftUuid();
                MinecraftBan existingBan = bans.getActive(minecraftUuid).join();
                if (existingBan != null) {
                    sender.sendMessage(MM."<red>That minecraft account is already banned!");
                    return;
                }

                long now = System.currentTimeMillis();
                MinecraftBan ban = new MinecraftBan(new ObjectId(), minecraftUuid, now, expiryMs);
                bans.insert(ban).join();

                proxyApiClient.kickOnAllProxies(minecraftUuid);

                if (ban.expiryTimestamp() == Long.MAX_VALUE) {
                    sender.sendMessage(MM."<yellow>Added <dark_red>perm ban <yellow>for <green>\{minecraftUuid}<yellow>!");
                } else {
                    sender.sendMessage(MM."<yellow>Added <red>ban <yellow>for <green>\{minecraftUuid} <yellow>expiring in <red>\{durationInput}<yellow>!");
                }
            }
        }, identifierArg, durationArg);
    }

    private static final Set<String> PERM_ALIASES = Sets.newHashSet("perm", "perma", "permanent");
    private long parseExpiry(String durationInput) {
        if (PERM_ALIASES.contains(durationInput.toLowerCase())) {
            return Long.MAX_VALUE;
        }

        long durationMs = DurationInput.parse(durationInput);
        return System.currentTimeMillis() + durationMs;
    }
}
