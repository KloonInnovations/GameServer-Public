package io.kloon.gameserver.commands.player;

import io.kloon.discord.client.DiscordNatsClient;
import io.kloon.discord.shared.DiscordLinkCommandReply;
import io.kloon.discord.shared.DiscordLinkCommandRequest;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import io.kloon.infra.mongo.accounts.KloonAccount;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class DiscordCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(DiscordCommand.class);

    private static final TextColor DISCORD_COLOR = TextColor.color(88, 101, 242);
    private final PlayerTickCooldownMap cooldown = new PlayerTickCooldownMap(3 * 20);

    public DiscordCommand() {
        super("discord");

        setDefaultExecutor(new CommandExecutor() {
            @Override
            public void apply(@NotNull CommandSender sender, @NotNull CommandContext context) {
                sender.sendMessage(MM."<#5865F2><b>JOIN OUR DISCORD SERVER!</b> <gray>It's a cool place! <#5865F2>[CLICK HERE]"
                        .hoverEvent(MM."<yellow>Click to get invite link!")
                        .clickEvent(ClickEvent.openUrl("https://discord.gg/peC3UVmZc6")));
            }
        });

        ArgumentWord linkIdArg = ArgumentType.Word("link id");
        addSyntax(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                if (!cooldown.get(player).cooldownIfPossible()) {
                    player.sendMessage(MM."<red>This command is on cooldown!");
                    return;
                }

                String linkId = context.get(linkIdArg);

                KloonAccount account = player.getAccount();

                DiscordNatsClient discord = Kgs.INSTANCE.getDiscord();
                DiscordLinkCommandRequest req = new DiscordLinkCommandRequest(account.getId().toHexString(), player.getUsername(), player.getUuid(), linkId);
                discord.useLinkCommand(req).whenCompleteAsync((res, t) -> {
                    if (t != null) {
                        player.sendPitError(MM."<gray>Error running command!");
                        LOG.error("Error with discord link command", t);
                        return;
                    }

                    DiscordLinkCommandReply.Status status = res.status();
                    if (status == DiscordLinkCommandReply.Status.LINK_NOT_FOUND) {
                        player.playSound(SoundEvent.ENTITY_WOLF_WHINE, 1.7);
                        player.sendPit(NamedTextColor.RED, "OOPS!", MM."<gray>That link cannot be found / has expired!");
                        return;
                    } else if (status == DiscordLinkCommandReply.Status.ALREADY_LINKED) {
                        player.playSound(SoundEvent.ENTITY_WOLF_WHINE, 1.7);
                        player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>That Discord account is linked to another Minecraft account!");
                        return;
                    } else if (status != DiscordLinkCommandReply.Status.SUCCESS) {
                        player.sendPitError(MM."<gray>Failed to link your Discord!");
                        LOG.error("Linking discord account wasn't a success");
                        return;
                    }

                    account.getConnections().getDiscord().setUserId(res.discordUserId());

                    String hex = DISCORD_COLOR.asHexString();
                    player.sendPit(DISCORD_COLOR, "DISCORD LINKED!", MM."<gray>With user <\{hex}>\{res.discordUsername()}<gray>!");
                    player.playSound(SoundEvent.BLOCK_AMETHYST_CLUSTER_STEP, 0.6);
                }, player.scheduler());
            }
        }, linkIdArg);
    }
}
