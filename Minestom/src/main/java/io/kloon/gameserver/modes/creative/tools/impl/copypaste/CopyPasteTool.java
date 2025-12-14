package io.kloon.gameserver.modes.creative.tools.impl.copypaste;

import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.OngoingChange;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.history.builtin.ReadVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.work.pasting.PastingWork;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.ReadVolumeWork;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.PlayerClipboard;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClip;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClipDetails;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.TickingTool;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.ToolMessages;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.commands.CopyCommand;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.commands.CopyPasteItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.menu.CopyPasteToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.selection.NoPasteSelection;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.selection.PasteSelection;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.selection.PastingSelection;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.command.builder.Command;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool.Preferences;

public class CopyPasteTool extends CreativeTool<CopyPasteSettings, Preferences> implements TickingTool {
    private static final Logger LOG = LoggerFactory.getLogger(CopyPasteTool.class);

    public CopyPasteTool() {
        super(CreativeToolType.COPY_PASTE, new ToolDataDef<>(CopyPasteSettings::new, CopyPasteSettings.class, Preferences::new, Preferences.class));
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        if (click.isLeftClick()) {
            pasteSelectionFromClipboard(player);
        } else {
            copySelectionToClipboard(player);
        }
    }

    private void pasteSelectionFromClipboard(CreativePlayer player) {
        PasteSelection selection = player.getPasteSelection();
        if (!(selection instanceof PastingSelection pastingSelection)) {
            player.sendPit(NamedTextColor.RED, "COPY FIRST!", MM."<gray>Nothing to paste!");
            player.playSound(SoundEvent.ENTITY_BREEZE_JUMP, 1.3f);
            return;
        }

        WorldClip clip = pastingSelection.getClip();
        int clipIndex = player.getClipboard().getClipIndex(clip);
        if (clipIndex < 0) {
            player.sendPit(NamedTextColor.RED, "MISSING CLIP!", MM."<gray>The clip in this tool wasn't found in your clipboard!");
            player.playSound(SoundEvent.ENTITY_BREEZE_JUMP, 1.3f);
            return;
        }

        CreativeInstance instance = player.getInstance();

        PastingWork work = new PastingWork(instance, pastingSelection.toPasting());
        BlocksJob blocksJob = instance.getJobQueue().trySubmit("Pasting Blocks", toolType, player, work);
        if (blocksJob == null) {
            return;
        }

        ChangeMeta changeMeta = new ChangeMeta(toolType, "<green>Pasting BLocks",
                MM."<gray>Pasted from clipboard #\{clipIndex + 1}",
                SoundEvent.ENTITY_BREEZE_INHALE, 1.8);
        player.getHistory().add(OngoingChange.fromJob(blocksJob, changeMeta));
        instance.addToAuditHistory(AuditRecord.create(player, ChangeType.APPLY_VOLUME, changeMeta));
        blocksJob.future().thenAccept(report -> {
            if (report.cancelled()) return;
            if (report.hadOutOfBounds()) {
                player.sendPit(NamedTextColor.RED, "OUT OF BOUNDS!", MM."<gray>Some blocks weren't pasted!");
                return;
            }

            ApplyVolumeChange change = (ApplyVolumeChange) report.change();
            int changedBlocks = change.getAfter().count();
            String changedBlocksFmt = NumberFmt.NO_DECIMAL.format(changedBlocks);

            Component contents = changedBlocks == 1
                    ? MM."<gray>Pasted \{changedBlocksFmt} block from clipboard #\{clipIndex + 1}!"
                    : MM."<gray>Pasted \{changedBlocksFmt} blocks from clipboard #\{clipIndex + 1}!";

            player.broadcast().send(MsgCat.TOOL,
                    NamedTextColor.GREEN, "PASTED!", contents,
                    SoundEvent.ENTITY_BREEZE_INHALE, Pitch.rng(1.8, 0.2));
        });
    }

    public void copySelectionToClipboard(CreativePlayer player) {
        if (!(player.getSelection() instanceof TwoCuboidSelection selection)) {
            ToolMessages.sendRequireSelection(player);
            return;
        }

        PlayerClipboard clipboard = player.getClipboard();
        int clipboardIndex = clipboard.getFirstUnusedIndex();
        if (clipboardIndex < 0) {
            player.sendPit(NamedTextColor.RED, "CLIPBOARD FULL!", MM."<gray>Your clipboard is full! Use F to open!");
            player.playSound(SoundEvent.ENTITY_BREEZE_JUMP, 1.3f);
            return;
        }

        if (clipboard.getCopyJob() != null) {
            player.sendPit(NamedTextColor.RED, "BUSY!", MM."<gray>Already copying something into the clipboard!");
            player.playSound(SoundEvent.ENTITY_BREEZE_JUMP, 1.3f);
            return;
        }

        CreativeInstance instance = player.getInstance();
        BoundingBox cuboid = selection.getCuboid();

        ReadVolumeWork work = new ReadVolumeWork(instance, cuboid);
        BlocksJob blocksJob = instance.getJobQueue().trySubmit("Copy Blocks", toolType, player, work);
        if (blocksJob == null) {
            return;
        }

        ItemRef itemRef = ItemRef.mainHand(player);

        blocksJob.future().whenCompleteAsync((completion, t) -> {
            if (completion.cancelled()) return;
            if (t != null) {
                LOG.error("Error copying blocks in copy/paste tool", t);
                player.sendPit(NamedTextColor.DARK_RED, "ERROR!", MM."<gray>Somehow there was an error copying the blocks!");
                return;
            }

            if (!(completion.change() instanceof ReadVolumeChange read)) {
                player.sendPit(NamedTextColor.DARK_RED, "ERROR!", MM."<gray>Something unexpected happened after copying the selection!");
                return;
            }

            if (clipboard.getClip(clipboardIndex) != null) {
                player.sendPit(NamedTextColor.DARK_RED, "CLIPBOARD BUSY!", MM."<gray>There's something in the intended clipboard #\{clipboardIndex}!");
                return;
            }

            WorldDef worldDef = instance.getWorldDef();
            WorldClipDetails details = new WorldClipDetails(
                    worldDef.name(),
                    worldDef._id()
            );

            WorldClip clip = new WorldClip(new ObjectId(), details, read.getVolume());
            player.getClipboard().setClip(clipboardIndex, clip);
            editItemBound(player, itemRef, s -> s.setClip(clip));

            player.msg().send(MsgCat.TOOL,
                    NamedTextColor.GREEN, "COPIED!", MM."<gray>Copied the selection into clipboard #\{clipboardIndex + 1}!",
                    SoundEvent.ENTITY_BREEZE_CHARGE, Pitch.rng(1.6, 0.2));
        }, player.scheduler());
    }

    @Override
    public void tickHolding(CreativePlayer player, ItemStack item) {
        PasteSelection selection = player.getPasteSelection();
        if (!player.canEditWorld()) {
            if (!(selection instanceof NoPasteSelection)) {
                selection.remove();
                player.setPasteSelection(new NoPasteSelection(player));
            }
            return;
        }

        CopyPasteSettings settings = getItemBound(item);
        WorldClip clip = settings.getClip(player);

        if (clip == null) {
            selection.remove();
            player.setPasteSelection(new NoPasteSelection(player));
        } else {
            boolean createNew = selection instanceof NoPasteSelection;
            boolean changeClip = selection instanceof PastingSelection pasting && clip != pasting.getClip();
            if (createNew || changeClip) {
                selection.remove();
                PastingSelection pasting = new PastingSelection(player, clip);
                player.setPasteSelection(pasting);
            }
        }

        player.getPasteSelection().tickHolding(settings);
    }

    @Override
    public List<Command> createCommands() {
        return List.of(
                new CopyCommand(this),
                new CopyPasteItemCommand(this)
        );
    }

    @Override
    public void writeUsage(List<Component> lore, CopyPasteSettings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>PASTE");
        lore.addAll(MM_WRAP."<gray>Paste from the clipboard onto the paste selection.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>COPY");
        lore.addAll(MM_WRAP."<gray>Copy your <selection> <gray>into your clipboard.");
    }

    @Override
    public void tickWithoutHolding(CreativePlayer player) {
        player.getPasteSelection().tickNotHolding();
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new CopyPasteToolMenu(this, itemRef).display(player);
    }

    public static class Preferences {

    }
}