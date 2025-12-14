package io.kloon.gameserver.modes.creative.menu.jobs;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.autoupdate.AutoUpdateMenu;
import io.kloon.gameserver.chestmenus.builtin.StaticButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.commands.jobs.JobsCommand;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.BlocksJobQueue;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.*;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class JobsManagementMenu extends ChestMenu implements AutoUpdateMenu {
    public static final String ICON = "\uD83C\uDFD7"; // 🏗️

    private final BlocksJobQueue jobsQueue;
    private final ChestMenu parent;

    private final Set<BlocksJob> knownJobs = new HashSet<>();

    public JobsManagementMenu(BlocksJobQueue jobsQueue, ChestMenu parent) {
        super(STR."\{ICON} Jobs Management");
        this.jobsQueue = jobsQueue;
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        knownJobs.addAll(jobsQueue.getJobs());
        List<BlocksJob> jobs = knownJobs.stream()
                .sorted(Comparator.comparingInt(BlocksJob::getTicketNumber))
                .toList();
        if (jobs.isEmpty()) {
            reg(22, new StaticButton(MenuStack.of(Material.BLACK_STAINED_GLASS)
                    .name(MM."<red>No ongoing jobs!")
                    .lore(MM_WRAP."<gray>It's a deafening silence.")));
        } else {
            ChestLayouts.INSIDE.distribute(jobs, (slot, job) -> {
                reg(slot, new JobButton(job, slot));
            });
        }

        reg().goBack(parent);
        reg(size.bottomCenter() + 1, CancelLatestJobButton::new);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<gold>\{ICON} <title>Jobs Management";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<cmd>\{JobsCommand.LABEL}");
        lore.add(Component.empty());

        lore.addAll(MM_WRAP."<gray>When using a tool to edit <b>lots</b> of blocks, the changes are queued into a job.");

        lore.add(Component.empty());
        lore.add(MM."<cta>Click to manage!");

        return MenuStack.of(Material.IRON_PICKAXE).name(name).lore(lore).build();
    }
}
