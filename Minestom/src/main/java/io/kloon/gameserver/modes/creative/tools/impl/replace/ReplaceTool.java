package io.kloon.gameserver.modes.creative.tools.impl.replace;

import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.OngoingChange;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.ReplaceWork;
import io.kloon.gameserver.modes.creative.menu.masks.MasksSelectionMenu;
import io.kloon.gameserver.modes.creative.selection.CuboidSelection;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.ToolMessages;
import io.kloon.gameserver.modes.creative.tools.impl.replace.commands.ReplaceCommand;
import io.kloon.gameserver.modes.creative.tools.impl.replace.commands.ReplaceItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.replace.menu.ReplaceToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementConfig;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementEntry;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.command.builder.Command;
import net.minestom.server.sound.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.replace.ReplaceTool.Preferences;
import static io.kloon.gameserver.modes.creative.tools.impl.replace.ReplaceTool.Settings;

public class ReplaceTool extends CreativeTool<Settings, Preferences> {
    private static final Logger LOG = LoggerFactory.getLogger(ReplaceTool.class);

    public ReplaceTool() {
        super(CreativeToolType.REPLACE, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        Settings settings = getItemBound(click.getItem());
        if (settings.replacement == null) {
            ToolMessages.sendRequireConfiguration(player);
            return;
        }

        ReplacementConfig replacementConfig;
        try {
            replacementConfig = MinecraftInputStream.fromBytes(settings.replacement, ReplacementConfig.CODEC);
        } catch (Throwable t) {
            LOG.error("Error loading block replacements", t);
            player.sendPit(NamedTextColor.DARK_RED, "ERROR!", MM."<gray>Couldn't read replacement configuration!");
            return;
        }

        replaceSelection(player, replacementConfig);
    }

    public void replaceSelection(CreativePlayer player, ReplacementConfig replacementConfig) {
        CuboidSelection selection = player.getSelection();
        if (!(selection instanceof TwoCuboidSelection completeSelection)) {
            ToolMessages.sendRequireSelection(player);
            return;
        }

        CreativeInstance instance = player.getInstance();
        BoundingBox cuboid = completeSelection.getCuboid();

        ReplaceWork work = new ReplaceWork(instance, cuboid, replacementConfig);
        BlocksJob blocksJob = instance.getJobQueue().trySubmit("Replace Blocks", toolType, player, work);
        if (blocksJob == null) {
            return;
        }

        ChangeMeta changeMeta = new ChangeMeta(toolType, "<light_purple>Selection Replace",
                MM."<gray>Replaced blocks within selection!",
                SoundEvent.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, 1.2);
        player.getHistory().add(OngoingChange.fromJob(blocksJob, changeMeta));
        instance.addToAuditHistory(AuditRecord.create(player, ChangeType.APPLY_VOLUME, changeMeta));
        blocksJob.future().thenAccept(report -> {
            if (report.cancelled()) return;

            ApplyVolumeChange change = (ApplyVolumeChange) report.change();
            int changedBlocks = change.getAfter().count();
            String changedBlocksFmt = NumberFmt.NO_DECIMAL.format(changedBlocks);

            Component contents = changedBlocks == 1
                    ? MM."<gray>Replaced \{changedBlocksFmt} block in the selection!"
                    : MM."<gray>Replaced \{changedBlocksFmt} blocks in the selection!";

            player.broadcast().send(MsgCat.TOOL,
                    NamedTextColor.LIGHT_PURPLE, "REPLACED!", contents,
                    SoundEvent.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, Pitch.rng(1.2, 0.5));
        });
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new ReplaceToolMenu(this, itemRef).display(player);
    }

    @Override
    public List<Command> createCommands() {
        return Arrays.asList(
                new ReplaceCommand(this),
                new ReplaceItemCommand(this)
        );
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        ReplacementConfig replacement = settings.getReplacementSafe();
        List<Component> replacementLore = replacement.lore().asList();
        lore.addAll(replacementLore);
        if (replacement.size() > 0) {
            lore.add(Component.empty());
        }

        lore.add(MM."\{InputFmt.CLICK_GREEN} <#FF266E><b>REPLACE");
        lore.addAll(MM_WRAP."<gray>Replaces blocks in your <selection><gray>.");

        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<red>\{MasksSelectionMenu.ICON} <gray>Ignores your masks!");

        if (settings.replacement == null) {
            lore.addAll(MM_WRAP."<yellow>\uD83D\uDCA1 <gray>Requires config before use!");
        }
    }

    public ReplacementConfig loadReplacementsSafe(ItemRef itemRef) {
        Settings settings = getItemBound(itemRef);
        return settings.getReplacementSafe();
    }

    public static class Settings {
        public byte[] replacement;

        public void setReplacement(ReplacementConfig replacementConfig) {
            this.replacement = MinecraftOutputStream.toBytesSneaky(replacementConfig, ReplacementConfig.CODEC);
        }

        public ReplacementConfig getReplacementSafe() {
            if (replacement == null) return ReplacementConfig.createDefault();
            try {
                return MinecraftInputStream.fromBytes(replacement, ReplacementConfig.CODEC);
            } catch (Throwable t) {
                LOG.error("Error loading block replacements", t);
                return new ReplacementConfig();
            }
        }
    }

    public static class Preferences {

    }
}
