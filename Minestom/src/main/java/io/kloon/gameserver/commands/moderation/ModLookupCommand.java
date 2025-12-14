package io.kloon.gameserver.commands.moderation;

import io.kloon.gameserver.commands.conditions.StaffRankCondition;
import io.kloon.gameserver.commands.executors.TargetAccountVirtualExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.formatting.TimeFmt;
import io.kloon.infra.mongo.accounts.KloonAccount;
import io.kloon.infra.ranks.StaffRank;
import net.kyori.adventure.text.event.ClickEvent;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ModLookupCommand extends Command {
    public ModLookupCommand() {
        super("lookup");
        setCondition(new StaffRankCondition(StaffRank.MODERATOR));

        ArgumentString identifierArg = ArgumentType.String("username or uuid");
        addSyntax(new TargetAccountVirtualExecutor(identifierArg) {
            @Override
            public void apply(@NotNull KloonPlayer sender, @NotNull CommandContext context, KloonAccount target) {
                sender.sendMessage(MM."<green>--- Mod Lookup ----");
                sender.sendMessage(MM."<white>Moniker: \{target.moniker().getDisplayMM()}");
                sender.sendMessage(MM."<white>UUID: <gray>\{target.getMinecraftUuid()}"
                        .hoverEvent(MM."<yellow>Click to paste in chat!")
                        .clickEvent(ClickEvent.suggestCommand(target.getMinecraftUuid().toString())));

                long timestampMs = target.getId().getTimestamp() * 1000L;
                sender.sendMessage(MM."<white>Joined: <#18D5FF>\{TimeFmt.date(timestampMs, "MMM dd yyyy")} <aqua>(\{TimeFmt.naturalTime(timestampMs)})");

                String discordId = target.getConnections().getDiscord().getUserId();
                if (discordId == null) {
                    sender.sendMessage(MM."<white>Discord: <red>Unlinked!");
                } else {
                    sender.sendMessage(MM."<white>Discord: <#AB47B0>\{discordId}"
                            .hoverEvent(MM."<yellow>Click to paste in chat!")
                            .clickEvent(ClickEvent.suggestCommand(discordId)));
                }
            }
        }, identifierArg);
    }
}
