package io.kloon.gameserver.modes.creative.commands.jobs;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.jobs.BlocksJobQueue;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.jobs.JobsManagementMenu;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class JobsCommand extends Command {
    public static final String LABEL = "jobs";

    public JobsCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                BlocksJobQueue jobQueue = player.getInstance().getJobQueue();
                CreativeMainMenu mainMenu = new CreativeMainMenu(player);
                new JobsManagementMenu(jobQueue, mainMenu).display(player);
            }
        });
    }
}
