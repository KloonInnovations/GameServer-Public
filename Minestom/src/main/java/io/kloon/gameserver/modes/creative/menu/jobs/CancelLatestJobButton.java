package io.kloon.gameserver.modes.creative.menu.jobs;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.jobs.CancelJobCommand;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class CancelLatestJobButton implements ChestButton {
    private final int slot;

    public CancelLatestJobButton(int slot) {
        this.slot = slot;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        CancelJobCommand.cancelLatestJob((CreativePlayer) player);
        ChestMenuInv.rerenderButton(slot, player);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<cmd>\{CancelJobCommand.LABEL}");
        lore.add(Component.empty());

        lore.addAll(MM_WRAP."<gray>Cancels the last job that <green>you <gray>submitted.");
        lore.add(Component.empty());

        lore.addAll(MM_WRAP."<gray>You may use <red>\{InputFmt.KEYBOARD} /\{CancelJobCommand.LABEL} [job number] <gray>to cancel a specific job.");
        lore.add(Component.empty());

        BlocksJob latestJob = CancelJobCommand.getLatestIncompleteJob(player);
        if (latestJob == null) {
            lore.add(MM."<dark_gray>No job to cancel!");
        } else {
            lore.add(MM."<gray>Latest job: <white>\{latestJob.getName()} <dark_gray>(\{latestJob.getTicketNumber()})");
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to cancel!");
        }

        return MenuStack.of(Material.TNT)
                .name(MM."<title>Cancel Latest Job")
                .lore(lore)
                .build();
    }
}
