package io.kloon.gameserver.commands.player;

import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.facts.KloonDataCenter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.network.packet.server.common.TransferPacket;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ProxyCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyCommand.class);

    private static final int MIN_SECONDS = 3;

    public ProxyCommand() {
        super("proxy");

        for (KloonDataCenter datacenter : KloonDataCenter.values()) {
            String hostname = datacenter.getHostname();
            if (hostname == null) continue;

            ArgumentLiteral dcArg = ArgumentType.Literal(datacenter.getShortLabel());
            addSyntax(new KloonExecutor() {
                @Override
                public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                    if (player.getProxyInfo().datacenter() == datacenter) {
                        player.sendPit(NamedTextColor.RED, "HEY!", MM."<gray>You're already connected through this proxy!");
                        player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 1.0);
                        return;
                    }

                    if (player.getAliveTicks() < MIN_SECONDS * 20) {
                        player.sendPit(NamedTextColor.RED, "OOPS!", MM."<gray>Can't use this within \{MIN_SECONDS}s of joining!");
                        player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 1.0);
                        return;
                    }

                    LOG.info(STR."player \{player.getUsername()} ran the \{getName()} command to \{datacenter.name()}");
                    player.sendMessage(MM."<gray>Reconnecting to \{hostname}...");
                    player.sendPacket(new TransferPacket(hostname, 25565));
                }
            }, dcArg);
        }
    }
}
