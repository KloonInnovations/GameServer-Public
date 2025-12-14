package io.kloon.gameserver.commands.testing;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamManager;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TestTeamCommand extends AdminCommand {
    public TestTeamCommand() {
        super("testteam");

        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                TeamManager teamMan = MinecraftServer.getTeamManager();
                Set<Team> teams = teamMan.getTeams();
                player.sendMessage("There are " + teams.size() + " teams");
                teams.forEach(team -> {
                    player.sendMessage(team.getTeamName() + " " + team.getMembers().size());
                    team.getMembers().forEach(member -> {
                        player.sendMessage(" member: " + member.replace("§", "$"));
                    });
                });
            }
        });
    }
}
