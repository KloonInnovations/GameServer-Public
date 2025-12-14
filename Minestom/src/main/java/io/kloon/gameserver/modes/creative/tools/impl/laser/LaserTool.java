package io.kloon.gameserver.modes.creative.tools.impl.laser;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.OngoingChange;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolSidebar;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.impl.laser.commands.LaserItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.laser.menu.LaserToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.LaserModeType;
import io.kloon.gameserver.modes.creative.tools.impl.laser.snipe.LaserSnipe;
import io.kloon.gameserver.modes.creative.tools.impl.laser.work.LaserGenSettings;
import io.kloon.gameserver.modes.creative.tools.impl.laser.work.LaserWork;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.laser.LaserTool.*;

public class LaserTool extends CreativeTool<LaserToolSettings, Preferences> {
    public static final String ICON = "/";
    public static final Color COLOR = new Color(255, 0, 0);

    public LaserTool() {
        super(CreativeToolType.LASER, new ToolDataDef<>(LaserToolSettings::new, LaserToolSettings.class, Preferences::new, Preferences.class));
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        CreativeInstance instance = player.getInstance();

        Vec start = Vec.fromPoint(player.getEyePosition());
        Vec end = player.getPointInFront(player.getSnipe().getRange());

        if (instance.isOutOfBounds(start) || instance.isOutOfBounds(end)) {
            player.sendOutOfBoundsMessage();
            return;
        }

        ItemStack inHand = player.getItemInMainHand();
        LaserToolSettings toolSettings = getItemBound(inHand);

        CreativePattern pattern = toolSettings.getPattern();
        boolean airPattern = pattern instanceof SingleBlockPattern single && single.getBlock().isAir();

        boolean ignoreBlocks = player.getCreativeStorage().getSnipe().isIgnoreBlocks()
                               || airPattern;

        LaserGenSettings genSettings = toolSettings.createGenSettings(start, end, ignoreBlocks, player.computeMaskLookup());
        LaserModeType mode = genSettings.mode();
        int length = (int) Math.round(end.distance(start));

        LaserWork work = new LaserWork(instance, genSettings);
        BlocksJob blocksJob = instance.getJobQueue().trySubmit(STR."Sniping \{mode.label()}", toolType, player, work);
        if (blocksJob == null) {
            return;
        }

        ChangeMeta changeMeta = new ChangeMeta(toolType, STR."Sniping \{mode.label()}",
                MM."<gray>Lasering a \{mode.label().toLowerCase()} of radius \{NumberFmt.NO_DECIMAL.format(genSettings.radius())} over \{length} blocks.",
                SoundEvent.BLOCK_END_PORTAL_FRAME_FILL, 0.6);
        player.getHistory().add(OngoingChange.fromJob(blocksJob, changeMeta));
        instance.addToAuditHistory(AuditRecord.create(player, ChangeType.APPLY_VOLUME, changeMeta));
        blocksJob.future().thenAccept(report -> {
            if (report.cancelled()) return;

            player.msg().send(MsgCat.TOOL,
                    NamedTextColor.RED, "LASER!", MM."<gray>\{mode.label()} with radius of \{NumberFmt.NO_DECIMAL.format(toolSettings.getRadius())} over \{length} blocks!",
                    SoundEvent.BLOCK_END_PORTAL_FRAME_FILL, Pitch.rng(0.5, 0.3));

            if (report.hadOutOfBounds()) {
                player.sendPit(NamedTextColor.RED, "OUT OF BOUNDS!", MM."<gray>Some blocks weren't generated!");
            }
        });
    }

    @Override
    public List<Command> createCommands() {
        return List.of(new LaserItemCommand(this));
    }

    @Override
    public void writeUsage(List<Component> lore, LaserToolSettings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>LASERIZE");
        lore.addAll(MM_WRAP."<gray>Draws a line of blocks from your eye position to your <snipe_target><gray>.");
    }

    @Override
    public @Nullable ToolSnipe<LaserToolSettings> createSnipe(CreativePlayer player) {
        return new LaserSnipe(player, this);
    }

    @Override
    public @Nullable ToolSidebar<LaserToolSettings, Preferences> createSidebar() {
        return new LaserSidebar();
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new LaserToolMenu(this, itemRef).display(player);
    }

    public static class Preferences {

    }
}
