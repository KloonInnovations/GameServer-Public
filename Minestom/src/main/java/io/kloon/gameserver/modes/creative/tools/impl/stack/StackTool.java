package io.kloon.gameserver.modes.creative.tools.impl.stack;

import com.spotify.futures.CompletableFutures;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.minestom.utils.PointFmt;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.OngoingChange;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.history.builtin.ReadVolumeChange;
import io.kloon.gameserver.modes.creative.history.builtin.SelectionChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.JobCompletion;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.ReadVolumeWork;
import io.kloon.gameserver.modes.creative.jobs.work.pasting.Pasting;
import io.kloon.gameserver.modes.creative.jobs.work.pasting.PastingWork;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.datainworld.SelectionCuboidStorage;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.ToolClickSide;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.ToolMessages;
import io.kloon.gameserver.modes.creative.tools.generics.selectionface.SelectionFaceHandler;
import io.kloon.gameserver.modes.creative.tools.generics.selectionface.SelectionFaceTool;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipFlip;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings.ClipRotation;
import io.kloon.gameserver.modes.creative.tools.impl.stack.commands.StackCommand;
import io.kloon.gameserver.modes.creative.tools.impl.stack.commands.StackItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.stack.menu.StackToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import io.kloon.gameserver.util.input.InputFmt;
import io.kloon.gameserver.util.physics.Collisions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.stack.StackTool.*;

public class StackTool extends CreativeTool<Settings, Preferences> implements TwoCuboidSelection.SelectionHighlighter, SelectionFaceTool<Settings> {
    private static final Logger LOG = LoggerFactory.getLogger(StackTool.class);
    private final SelectionFaceHandler<Settings, StackTool> selectionFaceHandler;

    public StackTool() {
        super(CreativeToolType.STACK, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
        this.selectionFaceHandler = new SelectionFaceHandler<>(this);
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        selectionFaceHandler.handleUse(player, click);
    }

    @Override
    public void handleClickFace(CreativePlayer player, ToolClick click, Settings settings, TwoCuboidSelection selection, CardinalDirection faceDir) {
        boolean pushing = click.side() == ToolClickSide.LEFT;
        int stacks = settings.getStacks();
        int offset = settings.getOffset();
        stackSelection(player, faceDir, pushing, stacks, offset);
    }

    public void stackSelection(CreativePlayer player, CardinalDirection faceNormal, boolean pushing, int stacks, int offset) {
        if (!(player.getSelection() instanceof TwoCuboidSelection selection)) {
            ToolMessages.sendRequireSelection(player);
            return;
        }

        CreativeInstance instance = player.getInstance();

        ReadVolumeWork readWork = new ReadVolumeWork(instance, selection.getCuboid());
        BlocksJob readJob = instance.getJobQueue().trySubmit("Copying Stack Blocks", toolType, player, readWork);
        if (readJob == null) {
            return;
        }

        Vec oldPos1 = selection.getPos1();
        Vec oldPos2 = selection.getPos2();
        Vec push = faceNormal.mul(pushing ? -1 : 1);

        BoundingBox originalCuboid = selection.getCuboid();
        SelectionCuboidStorage oldSelectionStorage = SelectionCuboidStorage.fromVec(oldPos1, oldPos2);

        int sizeAlongAxis = (int) BoundingBoxUtils.dimension(originalCuboid, faceNormal.axis());

        CardinalDirection actualDir = pushing ? faceNormal : faceNormal.reverse();
        String dirFmt = actualDir.name().toLowerCase();

        Preferences preferences = getPlayerBound(player);

        int maxStacks = 0;
        for (int i = 1; i <= stacks; ++i) {
            int moving = (i + 1) * (sizeAlongAxis + offset);
            Vec newPos1 = oldPos1.add(push.mul(moving));
            Vec newPos2 = oldPos2.add(push.mul(moving));
            if (instance.isOutOfBounds(newPos1) || instance.isOutOfBounds(newPos2)) {
                continue;
            }
            maxStacks = i;
        }

        if (maxStacks < stacks) {
            int stacksBefore = stacks;
            stacks = maxStacks;
            if (stacks > 0) {
                player.sendPit(NamedTextColor.RED, "STACKN'T!", MM."<gray>Adjusted from \{stacksBefore} to \{stacks} stacks because of world bounds!");
            }
        }
        if (stacks <= 0) {
            player.sendPit(NamedTextColor.RED, "STACKN'T!", MM."<gray>There's no room to stack in the \{dirFmt} direction!");
            player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 0.9);
            return;
        }

        final int fStacks = stacks;
        readJob.future().whenCompleteAsync((completion, t) -> {
            if (completion.cancelled()) return;
            if (t != null) {
                LOG.error("Error copying blocks in stack tool", t);
                player.sendPit(NamedTextColor.DARK_RED, "ERROR!", MM."<gray>Somehow there was an error copying the blocks!");
                return;
            }

            if (!(completion.change() instanceof ReadVolumeChange read)) {
                player.sendPit(NamedTextColor.DARK_RED, "ERROR!", MM."<gray>Something unexpected happened after copying the selection!");
                return;
            }

            BlockVolume volume = read.getVolume();

            List<BlocksJob> stackJobs = new ArrayList<>();
            List<Change> instantChanges = new ArrayList<>();

            for (int i = 1; i <= fStacks; ++i) {
                int moving = i * (sizeAlongAxis + offset);
                Vec newPos1 = oldPos1.add(push.mul(moving));
                Vec newPos2 = oldPos2.add(push.mul(moving));
                Vec pasteStart = newPos1.min(newPos2);

                Pasting pasting = new Pasting(volume, player.computeMaskLookup(), pasteStart, newPos1,
                        ClipRotation.ZERO, new ClipFlip(false, false, false),
                        false, false, false);

                PastingWork writeWork = new PastingWork(instance, pasting);
                BlocksJob writeJob = instance.getJobQueue().trySubmit("Pasting Stack Blocks #" + (i + 1), toolType, player, writeWork);
                if (writeJob != null) {
                    stackJobs.add(writeJob);
                }
            }

            if (preferences.isAdjustSelection()) {
                int lastMoving = fStacks * (sizeAlongAxis + offset);
                Vec lastPos1 = oldPos1.add(push.mul(lastMoving));
                Vec lastPos2 = oldPos2.add(push.mul(lastMoving));
                Vec overall1 = oldPos1.min(oldPos2).min(lastPos1).min(lastPos2);
                Vec overall2 = oldPos1.max(oldPos2).max(lastPos1).max(lastPos2);
                SelectionCuboidStorage newSelectionStorage = SelectionCuboidStorage.fromVec(overall1, overall2);

                player.setSelection(selection.adjust(overall1, overall2));
                SelectionChange selectionChange = new SelectionChange(oldSelectionStorage, newSelectionStorage);
                instantChanges.add(selectionChange);
            }

            ChangeMeta changeMeta = new ChangeMeta(toolType, "<green>Stacking Selection",
                    MM."<gray>Stacked selection \{fStacks}x towards \{dirFmt}",
                    SoundEvent.ENTITY_MULE_EAT, 0.6);

            OngoingChange historyChange = OngoingChange.fromJobsAndChanges(player.getUuid(), changeMeta, stackJobs, instantChanges);
            player.getHistory().add(historyChange);
            instance.addToAuditHistory(AuditRecord.create(player, ChangeType.APPLY_VOLUME, changeMeta));

            List<CompletableFuture<JobCompletion>> futures = stackJobs.stream()
                    .map(BlocksJob::future)
                    .toList();
            CompletableFutures.allAsList(futures).whenCompleteAsync((reports, t2) -> {
                if (t2 != null) {
                    player.sendPitError(MM."<white>Error while stacking blocks... awkward!");
                    LOG.error("error with stack tool", t2);
                    return;
                }

                boolean anyCancel = reports.stream().anyMatch(JobCompletion::cancelled);
                if (anyCancel) {
                    player.msg().send(MsgCat.NEGATIVE, NamedTextColor.RED, "CANCELLED!", MM."<gray>Some of the pasting jobs from the stack tool have been cancelled!");
                }
                boolean anyOutOfBounds = reports.stream().anyMatch(JobCompletion::hadOutOfBounds);
                if (anyOutOfBounds) {
                    player.msg().send(MsgCat.NEGATIVE, NamedTextColor.RED, "OUT OF BOUNDS!", MM."<gray>Some parts of the stacking job were out of bounds!");
                }

                Component details;
                if (fStacks == 1) {
                    details = MM."<gray>Selection of \{BoundingBoxUtils.fmtDimensions(originalCuboid)} towards \{dirFmt} once.";
                } else {
                    details = MM."<gray>Selection of \{BoundingBoxUtils.fmtDimensions(originalCuboid)} towards \{dirFmt} \{fStacks} times.";
                }
                player.broadcast().send(MsgCat.TOOL,
                        NamedTextColor.GREEN, "STACKED!", details,
                        changeMeta.coolSound());
            });
        }, player.scheduler());
    }

    @Override
    public Color getSelectionHighlightColor() {
        return new Color(255, 80, 110);
    }

    @Override
    public SoundEvent getHighlightSound() {
        return SoundEvent.BLOCK_ANCIENT_DEBRIS_FALL;
    }

    @Override
    public List<Command> createCommands() {
        return Arrays.asList(
                new StackCommand(this),
                new StackItemCommand(this)
        );
    }

    @Override
    public @Nullable Vec raycastFaceToHighlight(CreativePlayer player, BoundingBox boundingBox) {
        return Collisions.raycastBoxGetFaceNormal(player.getEyeRay(), boundingBox);
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new StackToolMenu(this, itemRef).display(player);
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>STACK");
        lore.addAll(MM_WRAP."<gray>Copies <selection> <gray>multiple times towards <gold>highlighted <gray>face");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>STACK (OTHER WAY)");
        lore.addAll(MM_WRAP."<gray>Same as stack, but the other way");
    }

    public static class Settings {
        private int stacks = 1;
        private int offset = 0;

        public int getStacks() {
            return stacks;
        }

        public void setStacks(int stacks) {
            this.stacks = stacks;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }

    public static class Preferences {
        private boolean adjustSelection;

        public boolean isAdjustSelection() {
            return adjustSelection;
        }

        public void setAdjustSelection(boolean adjustSelection) {
            this.adjustSelection = adjustSelection;
        }
    }
}
