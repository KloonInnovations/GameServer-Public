package io.kloon.gameserver.modes.creative.menu.jobs;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.BlocksJobQueue;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.util.formatting.BarFmt;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class JobButton implements ChestButton {
    private final BlocksJob job;
    private final int slot;

    public JobButton(BlocksJob job, int slot) {
        this.job = job;
        this.slot = slot;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        CreativeInstance instance = player.getInstance();

        if (job.isEnded()) {
            player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 0.8);
            Component msg = job.wasCancelled()
                    ? MM."<red>This job was already cancelled!"
                    : MM."<red>This job is already completed!";
            player.sendMessage(msg);
            return;
        }

        BlocksJobQueue jobQueue = instance.getJobQueue();
        jobQueue.tryCancelAndNotify(job, player);

        ChestMenuInv.rerenderButton(slot, player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        CreativeToolType toolType = job.getToolType();
        BlocksWork work = job.getWork();

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<gray>Job number: <yellow>#\{job.getTicketNumber()}");
        lore.add(MM."<gray>Tool: <aqua>\{job.getToolType().getDisplayName()}");
        lore.add(Component.empty());

        String secondsFmt = NumberFmt.ONE_DECIMAL.format(job.getETASeconds()) + "s";
        lore.add(MM."<gray>ETA: <yellow>\{secondsFmt}");

        int placed = work.getPlacedSoFar();
        int total = work.getTotalToPlace();
        double percent = total == 0 ? 1.0 : (double) placed / total;
        String barFmt = BarFmt.renderBar(7, percent);
        String percentFmt = NumberFmt.NO_DECIMAL.format(percent * 100) + "%";
        lore.add(MM."<gray>Progress: \{barFmt} <#FF266E>\{percentFmt}");
        lore.add(MM."<gray>Blocks: <green>\{NumberFmt.NO_DECIMAL.format(placed)}/\{NumberFmt.NO_DECIMAL.format(total)}");
        lore.add(Component.empty());

        Material icon;
        if (job.isEnded()) {
            icon = Material.GRAY_DYE;
            if (job.wasCancelled()) {
                lore.add(MM."<red>Job cancelled!");
            } else {
                lore.add(MM."<dark_gray>Job is complete!");
            }
        } else {
            icon = toolType.getMaterial();
            lore.add(MM."<cta>Click to <red>cancel<yellow>!");
        }

        return MenuStack.of(icon)
                .name(MM."<white>\{job.getName()} <dark_gray>(\{job.getTicketNumber()})")
                .lore(lore)
                .build();
    }
}
