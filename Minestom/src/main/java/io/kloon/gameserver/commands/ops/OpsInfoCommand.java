package io.kloon.gameserver.commands.ops;

import io.kloon.gameserver.Kgs;
import io.kloon.infra.KloonNetworkInfra;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpsInfoCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(OpsInfoCommand.class);

    public OpsInfoCommand() {
        super("info");
        setDefaultExecutor((sender, context) -> {
            KloonNetworkInfra infra = Kgs.getInfra();
            send(sender, "--------------");
            send(sender, STR."OPS INFO, dc=\{infra.datacenter().getDbKey()} env=\{infra.environment().getDbKey()}");
            send(sender, STR."alloc=\{infra.allocationName()}");
            send(sender, STR."pid=\{ProcessHandle.current().pid()}");
            send(sender, "--------------");
        });
    }

    private void send(CommandSender sender, String msg) {
        sender.sendMessage(msg);
        LOG.info(msg);
    }
}
