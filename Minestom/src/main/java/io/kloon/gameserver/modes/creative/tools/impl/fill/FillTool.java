package io.kloon.gameserver.modes.creative.tools.impl.fill;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.minestom.utils.PointFmt;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.OngoingChange;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.SetCuboidPatternWork;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.SetCuboidWork;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.impl.BlockToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ItemBoundPattern;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.ToolMessages;
import io.kloon.gameserver.modes.creative.tools.impl.fill.commands.FillCommand;
import io.kloon.gameserver.modes.creative.tools.impl.fill.commands.FillItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.fill.menu.FillToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.fill.work.FloodFillBlock;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.fill.FillTool.Preferences;
import static io.kloon.gameserver.modes.creative.tools.impl.fill.FillTool.Settings;

public class FillTool extends CreativeTool<Settings, Preferences> {
    private static final Logger LOG = LoggerFactory.getLogger(FillTool.class);

    public FillTool() {
        super(CreativeToolType.FILL, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        if (player.isSneaking()) {
            pickBlock(player, click);
        } else if (click.isRightClick()) {
            fillSelection(player, click);
        } else {
            floodFill(player, click);
        }
    }

    private void floodFill(CreativePlayer player, ToolClick click) {
        Point targetBlock = player.getTargetBlockPosition(50);
        if (targetBlock == null) {
            player.sendPit(NamedTextColor.RED, "ZOOP!", MM."<gray>Did you click something?");
            player.playSound(SoundEvent.ENTITY_BEE_HURT, Pitch.rng(0.7, 0.15));
            return;
        }

        CreativeInstance instance = player.getInstance();
        Settings settings = getItemBound(click);

        CreativePattern pattern = settings.getPattern();
        if (pattern == null) {
            ToolMessages.sendRequireConfiguration(player);
            return;
        }
        String blockName = pattern.labelMM();

        MaskLookup mask = player.computeMaskLookup();

        BlocksWork work = new FloodFillBlock(instance, targetBlock, pattern, mask);
        BlocksJob blocksJob = instance.getJobQueue().trySubmit("Flood-Fill", toolType, player, work);
        if (blocksJob == null) {
            return;
        }

        ChangeMeta changeMeta = new ChangeMeta(toolType, "<aqua>Flood-Fill",
                MM."<gray>Flood filled target from \{PointFmt.fmt10k(targetBlock)} <gray>into \{ blockName }<gray>.",
                SoundEvent.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 1.4);
        player.getHistory().add(OngoingChange.fromJob(blocksJob, changeMeta));
        instance.addToAuditHistory(AuditRecord.create(player, ChangeType.APPLY_VOLUME, changeMeta));
        blocksJob.future().thenAccept(report -> {
            if (report.cancelled()) return;

            ApplyVolumeChange change = (ApplyVolumeChange) report.change();
            int changed = change.getAfter().count();
            String changedFmt = NumberFormat.getInstance().format(changed);
            Component contents = changed == 1
                    ? MM."<gray>\{changedFmt} block with \{blockName}!"
                    : MM."<gray>\{changedFmt} blocks with \{blockName}!";

            player.msg().send(MsgCat.TOOL,
                    NamedTextColor.AQUA, "FLOOD-FILL!", contents,
                    SoundEvent.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, Pitch.rng(1.8, 0.2));
        });
    }

    private void fillSelection(CreativePlayer player, ToolClick click) {
        Settings settings = getItemBound(click.getItem());
        CreativePattern pattern = settings.getPattern();
        if (pattern == null) {
            ToolMessages.sendRequireConfiguration(player);
            return;
        }

        fillSelection(player, pattern);
    }

    public void fillSelection(CreativePlayer player, CreativePattern pattern) {
        if (!(player.getSelection() instanceof TwoCuboidSelection selection)) {
            ToolMessages.sendRequireSelection(player);
            return;
        }

        CreativeInstance instance = player.getInstance();

        String blockName = pattern.labelMM();

        BoundingBox cuboid = selection.getCuboid();
        MaskLookup mask = player.computeMaskLookup();

        boolean simpleCuboid = pattern instanceof SingleBlockPattern && mask.isPassthrough();

        BlocksWork work = simpleCuboid
                ? new SetCuboidWork(instance, cuboid, ((SingleBlockPattern) pattern).getBlock())
                : new SetCuboidPatternWork(instance, cuboid, pattern, mask);
        ChangeType changeType = simpleCuboid ? ChangeType.SET_CUBOID : ChangeType.APPLY_VOLUME;

        String jobName = STR."Fill \{blockName}";
        BlocksJob blocksJob = instance.getJobQueue().trySubmit(jobName, toolType, player, work);
        if (blocksJob == null) {
            return;
        }

        ChangeMeta changeMeta = new ChangeMeta(toolType, "<aqua>Selection Fill",
                MM."<gray>Filled selection with \{blockName}!",
                SoundEvent.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.3);
        player.getHistory().add(OngoingChange.fromJob(blocksJob, changeMeta));
        instance.addToAuditHistory(new AuditRecord(System.currentTimeMillis(), player.getAccountId(), changeType, changeMeta));
        blocksJob.future().thenAccept(report -> {
            if (report.cancelled()) return;

            long volume = BoundingBoxUtils.volumeRounded(cuboid);
            if (report.change() instanceof ApplyVolumeChange volumeChange) {
                volume = volumeChange.getAfter().count();
            }

            String volumeFmt = NumberFormat.getInstance().format(volume);
            Component contents = volume == 1
                    ? MM."<gray>\{volumeFmt} block in selection with \{blockName}!"
                    : MM."<gray>\{volumeFmt} blocks in selection with \{blockName}!";
            player.broadcast().send(MsgCat.TOOL,
                    NamedTextColor.AQUA, "FILL!", contents,
                    SoundEvent.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, Pitch.rng(1.3, 0.7));
        });
    }

    private void pickBlock(CreativePlayer player, ToolClick click) {
        Block block = null;
        if (click instanceof BlockToolClick blockClick) {
            Vec blockPos = blockClick.getBlockPos();
            block = player.getInstance().getBlock(blockPos);
        }
        if (block == null) {
            player.sendPit(NamedTextColor.RED, "AYO", MM."<gray>Click on a block if you be sneaking with that!");
            player.playSound(SoundEvent.ENTITY_BEE_HURT, 1.7);
            return;
        }

        ItemRef itemRef = ItemRef.mainHand(player);

        final Block fBlock = block;
        boolean edited = editItemBound(player, itemRef, settings -> settings.setPattern(new SingleBlockPattern(fBlock)));
        if (edited) {
            ToolDataType.ITEM_BOUND.sendMsg(player,
                    MM."<gray>Updated fill block to \{TinkeredBlock.getNameMM(block)}!",
                    SoundEvent.ENTITY_AXOLOTL_SPLASH, 2, 0.7);
        }
    }

    @Override
    public List<Command> createCommands() {
        return Arrays.asList(
                new FillCommand(this),
                new FillItemCommand(this)
        );
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new FillToolMenu(this, itemRef).display(player);
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        CreativePattern fillPattern = settings.getPattern();

        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>FLOOD-FILL");
        lore.addAll(MM_WRAP."<gray>Recursively sets connected blocks from your target.");
        lore.add(MM."<dark_gray>Ignores block match masks.");
        lore.add(Component.empty());

        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>FILL SELECTION");
        lore.addAll(MM_WRAP."<gray>Sets every block in your <selection><gray>.");

        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.SNEAK_CLICK_GREEN} <#FF266E><b>PICK BLOCK");
        lore.addAll(MM_WRAP."<gray>Sets fill block to target block.");

        if (fillPattern == null) {
            lore.add(Component.empty());
            lore.addAll(MM_WRAP."<yellow>\uD83D\uDCA1 <gray>Requires config before use!");
        }
    }

    @Override
    public @Nullable ItemStack renderOverride(Settings settings, Preferences preferences) {
        ItemBuilder2 builder = toolBuilder(settings, settings.getIcon().build().material());

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        writeUsage(lore, settings, preferences);
        builder.lore(lore);

        CreativePattern pattern = settings.getPattern();
        if (pattern != null) {
            builder.name(MM."<white>Fill Tool (\{pattern.labelMM()}<white>)");
            builder.glowing();
        }

        return builder.build();
    }

    public static class Settings implements ItemBoundPattern {
        private byte[] pattern;

        @Override
        public boolean hasPattern() {
            return pattern != null;
        }

        @Override
        @Nullable
        public CreativePattern getPattern() {
            if (pattern == null) return null;
            return MinecraftInputStream.fromBytesSneaky(pattern, CreativePattern.CODEC);
        }

        @Override
        public void setPattern(CreativePattern pattern) {
            this.pattern = MinecraftOutputStream.toBytesSneaky(pattern, CreativePattern.CODEC);
        }

        public ItemBuilder2 getIcon() {
            CreativePattern pattern = getPattern();
            if (pattern == null) return MenuStack.of(Material.BUCKET);
            return pattern.icon();
        }
    }

    public record Preferences() {
    }
}
