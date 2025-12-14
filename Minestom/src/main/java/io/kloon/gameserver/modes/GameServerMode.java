package io.kloon.gameserver.modes;

import io.kloon.gameserver.KloonGameServer;
import io.kloon.gameserver.commands.admin.AdminCommands;
import io.kloon.gameserver.commands.moderation.ModeratorCommands;
import io.kloon.gameserver.commands.ops.OpsCommand;
import io.kloon.gameserver.commands.player.ProxyCommand;
import io.kloon.gameserver.commands.player.block.BlockCommand;
import io.kloon.gameserver.commands.player.block.UnblockCommand;
import io.kloon.gameserver.commands.testing.*;
import io.kloon.gameserver.commands.player.DiscordCommand;
import io.kloon.gameserver.commands.player.JoinCommand;
import io.kloon.infra.KloonNetworkInfra;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameServerMode {
    protected final Logger LOG;
    protected final KloonNetworkInfra infra;

    public GameServerMode(KloonNetworkInfra infra) {
        this.LOG = LoggerFactory.getLogger(getClass());
        this.infra = infra;
    }

    public void onMinestomInitialize() {
        CommandManager commandMan = MinecraftServer.getCommandManager();
        commandMan.register(new AdminCommands());
        commandMan.register(new OpsCommand());
        commandMan.register(new ModeratorCommands());

        commandMan.register(new GameModeCommand());
        commandMan.register(new TimeSetCommand());
        commandMan.register(new TestTeamCommand());
        commandMan.register(new TestExceptionCommand());
        commandMan.register(new MMCommand());
        commandMan.register(new TestBlockSelectionCommand());
        commandMan.register(new KnownPropertiesCommand());
        commandMan.register(new SmallcapsCommand());
        commandMan.register(new TestBanner());
        commandMan.register(new FakeBuyCommand());

        commandMan.register(new BlockCommand());
        commandMan.register(new UnblockCommand());
        commandMan.register(new JoinCommand());
        commandMan.register(new DiscordCommand());
        commandMan.register(new ProxyCommand());
    }

    public abstract void onStart(KloonGameServer kgs);

    public abstract ModeType getType();

    public int getMaxPlayers() {
        return 20;
    }
}
