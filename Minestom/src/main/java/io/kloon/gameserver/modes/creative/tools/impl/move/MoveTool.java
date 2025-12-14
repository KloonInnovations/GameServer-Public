package io.kloon.gameserver.modes.creative.tools.impl.move;

import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.OngoingChange;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.history.builtin.ReadVolumeChange;
import io.kloon.gameserver.modes.creative.history.builtin.SelectionChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.ReadVolumeWork;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.SetCuboidWork;
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
import io.kloon.gameserver.modes.creative.tools.impl.move.commands.MoveCommand;
import io.kloon.gameserver.modes.creative.tools.impl.move.commands.MoveItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.move.commands.MoveXCommand;
import io.kloon.gameserver.modes.creative.tools.impl.move.menu.MoveToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.coordinates.Axis;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.gameserver.util.input.InputFmt;
import io.kloon.gameserver.util.physics.BoundingBoxFace;
import io.kloon.gameserver.util.physics.Collisions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.move.MoveTool.Preferences;
import static io.kloon.gameserver.modes.creative.tools.impl.move.MoveTool.Settings;

public class MoveTool extends CreativeTool<Settings, Preferences> implements TwoCuboidSelection.SelectionHighlighter, SelectionFaceTool<Settings> {
    private static final Logger LOG = LoggerFactory.getLogger(MoveTool.class);

    private final SelectionFaceHandler<Settings, MoveTool> selectionFaceHandler;

    public MoveTool() {
        super(CreativeToolType.MOVE, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
        this.selectionFaceHandler = new SelectionFaceHandler<>(this);
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        selectionFaceHandler.handleUse(player, click);
    }

    @Override
    public void handleClickFace(CreativePlayer player, ToolClick click, Settings settings, TwoCuboidSelection selection, CardinalDirection faceNormal) {
        boolean pushing = click.side() == ToolClickSide.LEFT;

        int distance;
        if (settings.moveMultiples) {
            double size = BoundingBoxUtils.dimension(selection.getCuboid(), faceNormal.axis());
            distance = (int) (settings.multiples * size);
        } else {
            distance = settings.blocks;
        }

        Vec push = faceNormal.vec().mul(-distance);
        CardinalDirection pushDir = CardinalDirection.fromVec(push);

        moveSelection(player, pushDir, distance, pushing);
    }

    public void moveSelection(CreativePlayer player, CardinalDirection pushDir, int distance, boolean pushing) {
        if (!(player.getSelection() instanceof TwoCuboidSelection selection)) {
            ToolMessages.sendRequireSelection(player);
            return;
        }

        CreativeInstance instance = player.getInstance();

        Vec oldPos1 = selection.getPos1();
        Vec oldPos2 = selection.getPos2();

        Vec push = pushDir.mul(distance).mul(pushing ? 1 : -1);

        Vec newPos1 = oldPos1.add(push);
        Vec newPos2 = oldPos2.add(push);

        SelectionCuboidStorage oldSelectionStorage = SelectionCuboidStorage.fromVec(oldPos1, oldPos2);
        SelectionCuboidStorage newSelectionStorage = SelectionCuboidStorage.fromVec(newPos1, newPos2);

        if (instance.isOutOfBounds(newPos1) || instance.isOutOfBounds(newPos2)) {
            player.sendOutOfBoundsMessage();
            return;
        }

        Vec pasteStart = newPos1.min(newPos2);

        ReadVolumeWork readWork = new ReadVolumeWork(instance, selection.getCuboid());
        BlocksJob readJob = instance.getJobQueue().trySubmit("Copying Move Blocks", toolType, player, readWork);
        if (readJob == null) {
            return;
        }

        ItemRef itemRef = ItemRef.mainHand(player);
        Settings settings = getItemBound(itemRef);

        readJob.future().whenCompleteAsync((completion, t) -> {
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

            BlockVolume volume = read.getVolume();
            Pasting pasting = new Pasting(volume, player.computeMaskLookup(), pasteStart, newPos1,
                    ClipRotation.ZERO, new ClipFlip(false, false, false),
                    settings.isIgnorePasteAir(), settings.isIgnoreMasks(), false);

            List<BlocksJob> moveJobs = new ArrayList<>();

            PastingWork writeWork = new PastingWork(instance, pasting);
            BlocksJob writeJob = instance.getJobQueue().trySubmit("Pasting Move Blocks", toolType, player, writeWork);
            if (writeJob == null) {
                return;
            }
            moveJobs.add(writeJob);

            player.setSelection(selection.adjust(newPos1, newPos2));
            SelectionChange selectionChange = new SelectionChange(oldSelectionStorage, newSelectionStorage);

            if (settings.cut) {
                BoundingBox clearCuboid = computeCutCuboid(selection.getCuboid(), push);
                SetCuboidWork clearWork = new SetCuboidWork(instance, clearCuboid, Block.AIR);
                BlocksJob cutJob = instance.getJobQueue().trySubmit("Cutting Move Blocks", toolType, player, clearWork);
                if (cutJob != null) {
                    moveJobs.add(cutJob);
                }
            }

            ChangeMeta changeMeta = new ChangeMeta(toolType, "<green>Moving Blocks",
                    MM."<gray>Moved blocks and selection",
                    SoundEvent.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, 1.8);

            OngoingChange historyChange = OngoingChange.fromJobsAndChanges(player.getUuid(), changeMeta,
                    moveJobs,
                    Arrays.asList(selectionChange));
            player.getHistory().add(historyChange);

            instance.addToAuditHistory(AuditRecord.create(player, ChangeType.APPLY_VOLUME, changeMeta));

            writeJob.future().thenAccept(report -> {
                if (report.cancelled()) return;
                if (report.hadOutOfBounds()) {
                    player.sendPit(NamedTextColor.RED, "OUT OF BOUNDS!", MM."<gray>Some blocks weren't moved over!");
                    return;
                }

                ApplyVolumeChange volumeChange = (ApplyVolumeChange) report.change();

                int changedBlocks = volumeChange.getAfter().count();
                String changedBlocksFmt = NumberFmt.NO_DECIMAL.format(changedBlocks);

                CardinalDirection actualDir = pushing ? pushDir : pushDir.reverse();
                String dirFmt = actualDir.name().toLowerCase();
                Component contents;
                if (distance == 1) {
                    contents = changedBlocks == 1
                            ? MM."<gray>Moved \{changedBlocksFmt} block towards \{dirFmt} by \{distance} block!"
                            : MM."<gray>Moved \{changedBlocksFmt} blocks towards \{dirFmt} by \{distance} block!";
                } else {
                    contents = changedBlocks == 1
                            ? MM."<gray>Moved \{changedBlocksFmt} block towards \{dirFmt} by \{distance} blocks!"
                            : MM."<gray>Moved \{changedBlocksFmt} blocks towards \{dirFmt} by \{distance} blocks!";
                }

                player.broadcast().send(MsgCat.TOOL,
                        NamedTextColor.DARK_GREEN, "MOVED!", contents,
                        SoundEvent.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, 1.8);
            });
        }, player.scheduler());
    }

    private BoundingBox computeCutCuboid(BoundingBox boundingBox, Vec push) {
        Vec pushNorm = push.normalize();

        double pushLength = push.length();
        double lengthAlongPush = BoundingBoxUtils.dimension(boundingBox, Axis.byVec(push));

        BoundingBoxFace face = BoundingBoxUtils.computeFaceFromNormal(boundingBox, pushNorm.mul(-1));

        Vec a = face.min();
        Vec b = face.max().add(pushNorm.mul(Math.min(pushLength, lengthAlongPush)));

        return BoundingBox.fromPoints(a, b);
    }

    @Override
    public Color getSelectionHighlightColor() {
        return new Color(211, 148, 211);
    }

    @Override
    public SoundEvent getHighlightSound() {
        return SoundEvent.ENTITY_SLIME_SQUISH_SMALL;
    }

    @Override
    public @Nullable Vec raycastFaceToHighlight(CreativePlayer player, BoundingBox boundingBox) {
        return Collisions.raycastBoxGetFaceNormal(player.getEyeRay(), boundingBox);
    }

    @Override
    public List<Command> createCommands() {
        return Arrays.asList(
                new MoveCommand(this),
                new MoveXCommand(this),
                new MoveItemCommand(this)
        );
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new MoveToolMenu(this, itemRef).display(player);
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>MOVE");
        lore.addAll(MM_WRAP."<gray>Pushes <selection> <gray>and the blocks within towards <gold>highlighted <gray>face.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>MOVE (OTHER WAY)");
        lore.addAll(MM_WRAP."<gray>Same as move, but the other way");
    }

    public static class Settings {
        public int blocks = 1;
        public int multiples = 1;
        public boolean moveMultiples = false;
        private boolean ignoreMasks = true;
        private boolean ignorePasteAir = true;
        private boolean cut = true;

        public boolean isIgnoreMasks() {
            return ignoreMasks;
        }

        public void setIgnoreMasks(boolean ignoreMasks) {
            this.ignoreMasks = ignoreMasks;
        }

        public boolean isIgnorePasteAir() {
            return ignorePasteAir;
        }

        public void setIgnorePasteAir(boolean ignorePasteAir) {
            this.ignorePasteAir = ignorePasteAir;
        }

        public boolean isCut() {
            return cut;
        }

        public void setCut(boolean cut) {
            this.cut = cut;
        }
    }

    public static class Preferences {

    }
}
