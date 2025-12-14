package io.kloon.gameserver.modes.creative.tools.generics.snipe;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.OngoingChange;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.item.ItemStack;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SnipeWorkHandler<ItemBound, Tool extends CreativeTool<ItemBound, ?> & SnipeWorkTool<ItemBound>> {
    private final String jobTitle;
    protected final Tool tool;

    public SnipeWorkHandler(String jobTitle, Tool tool) {
        this.jobTitle = jobTitle;
        this.tool = tool;
    }

    public void handleUse(CreativePlayer player, ToolClick click) {
        CreativeInstance instance = player.getInstance();
        BlockVec targetPos = player.getSnipe().computeTarget();

        if (instance.isOutOfBounds(targetPos)) {
            player.sendOutOfBoundsMessage();
            return;
        }

        ItemStack inHand = player.getItemInMainHand();
        ItemBound itemBound = tool.getItemBound(inHand);

        BlocksWork work = tool.createWork(player, click, targetPos, itemBound);
        BlocksJob blocksJob = instance.getJobQueue().trySubmit(jobTitle, tool.getType(), player, work);
        if (blocksJob == null) {
            return;
        }

        ChangeMeta changeMeta = tool.createChangeMeta(player, click, itemBound);
        player.getHistory().add(OngoingChange.fromJob(blocksJob, changeMeta));
        instance.addToAuditHistory(AuditRecord.create(player, ChangeType.APPLY_VOLUME, changeMeta));

        blocksJob.future().thenAccept(report -> {
            if (report.cancelled()) return;

            tool.onJobComplete(player, click, itemBound, report.change());

            if (report.hadOutOfBounds()) {
                player.sendPit(NamedTextColor.RED, "OUT OF BOUNDS!", MM."<gray>Some blocks weren't processed!");
            }
        });
    }
}
