package io.kloon.gameserver.modes.creative.tools.impl.tinker;

import io.kloon.gameserver.minestom.blocks.KloonPlacementRules;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.builtin.BlockChange;
import io.kloon.gameserver.modes.creative.menu.patterns.tinker.TinkerBlockMenu;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.impl.BlockToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.commands.TinkerCommand;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandlers;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.menu.TinkerToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.RandUtil;
import io.kloon.gameserver.util.input.InputFmt;
import io.kloon.gameserver.util.physics.Collisions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;

import java.util.Arrays;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkerTool.*;

public class TinkerTool extends CreativeTool<Settings, Preferences> {
    private final TinkerEditHandlers editHandlers = new TinkerEditHandlers();

    public TinkerTool() {
        super(CreativeToolType.TINKER, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        if (player.isSneaking()) {
            handleBlockPick(player, click);
        } else if (click.isRightClick()) {
            handleEditBlock(player, click);
        } else {
            handleOpenEditMenu(player, click);
        }
    }

    private void handleBlockPick(CreativePlayer player, ToolClick click) {
        Point blockPos = player.getTargetBlockPosition(64);
        if (blockPos == null) {
            player.playSound(SoundEvent.ENTITY_ARMADILLO_BRUSH, Pitch.rng(1.9, 0.1));
            return;
        }

        CreativeInstance instance = player.getInstance();
        Block block = instance.getBlock(blockPos);
        ItemStack item = new TinkeredBlock(block).toItem();

        player.msg().send(MsgCat.INVENTORY,
                NamedTextColor.GREEN, "PICKED UP!", MM."<gray>Added \{TinkeredBlock.getNameMM(block)} <gray>to your inventory!",
                SoundEvent.ENTITY_ARMADILLO_BRUSH, Pitch.rng(1.5, 0.2));

        player.getInventoryExtras().grab(item);
    }

    private void handleEditBlock(CreativePlayer player, ToolClick click) {
        if (!(click instanceof BlockToolClick blockClick)) {
            return;
        }
        BlockVec blockPos = new BlockVec(blockClick.getBlockPos());
        Block block = player.getInstance().getBlock(blockPos);

        Vec raycastEntry = Collisions.raycastBoxGetPoint(player.getEyeRay(), BoundingBoxUtils.fromBlock(blockPos));
        if (raycastEntry == null) {
            raycastEntry = new Vec(0, 0, 0);
        } else {
            raycastEntry = raycastEntry.sub(blockPos);
        }

        Vec cursorPos = blockClick.getCursorPos();
        if (cursorPos == null) {
            cursorPos = raycastEntry;
        }

        TinkerEditHandler editHandler = editHandlers.get(block);
        if (editHandler == null) {
            player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_BASEDRUM, Pitch.rng(0.9, 0.2), 0.15);
            //player.sendPit(MiniMessageTemplate.TITLE_COLOR, "TINKER", MM."<gray>Mmh... nothing happens from clicking \{BlockFmt.getName(block)}...");
        } else {
            block = editHandler.edit(blockPos, cursorPos, raycastEntry, block);
            if (block == null) {
                player.sendPitError(MM."<gray>Missing tinker implementation!");
            } else {
                player.getInstance().setBlock(blockPos, block);
                player.playSound(SoundEvent.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON, Pitch.rng(0.5, 0.32), 0.15);
            }
        }
    }

    private void handleOpenEditMenu(CreativePlayer player, ToolClick click) {
        Point blockPos = player.getTargetBlockPosition(64);
        if (blockPos == null) {
            player.playSound(SoundEvent.ENTITY_ARMADILLO_BRUSH, Pitch.rng(1.9, 0.1));
            return;
        }

        CreativeInstance instance = player.getInstance();
        Block block = instance.getBlock(blockPos);
        if (block.properties().isEmpty()) {
            player.sendPit(NamedTextColor.RED, RandUtil.getRandom(CONFUSION), MM."<white>\{BlockFmt.getName(block)} <gray>can't be tinkered!");
            player.playSound(SoundEvent.ENTITY_ARMADILLO_BRUSH, Pitch.rng(1.5, 0.35));
            return;
        }

        CreativeConsumer<Block> editBlock = (_, editedBlock) -> {
            Block blockBefore = instance.getBlock(blockPos);
            editedBlock = KloonPlacementRules.injectHandler(editedBlock);
            instance.setBlock(blockPos, editedBlock);

            CoolSound sound = player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_CHIME, computePitch(editedBlock));
            player.addToHistory(CreativeToolType.TINKER, "Tinkered Block",
                    MM."<light_purple>Edited \{BlockFmt.getName(editedBlock)}", sound,
                    new BlockChange(blockPos, blockBefore, editedBlock));
        };

        player.playSound(SoundEvent.ENTITY_ARMADILLO_BRUSH, Pitch.rng(0.5, 0.35));

        new TinkerBlockMenu(null, block)
                .withOnEdit(editBlock)
                .display(player);
    }

    private double computePitch(Block block) {
        int diff = Math.abs(block.stateId() - block.defaultState().stateId());
        int available = block.possibleStates().size();
        return 0.6 + ((double) diff / available) * 1.3;
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>BLOCK MENU");
        lore.addAll(MM_WRAP."<gray>Opens a menu to edit the target block's properties.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>EDIT BLOCK");
        lore.addAll(MM_WRAP."<gray>Edits the target block, depending on the block and where you clicked.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.SNEAK_CLICK_GREEN} <#FF266E><b>PICK BLOCK");
        lore.addAll(MM_WRAP."<gray>Copies and adds the target block to your inventory.");
    }

    @Override
    public void openSettingsMenu(CreativePlayer p, ItemRef itemRef) {
        new TinkerToolMenu(this, itemRef).display(p);
    }

    @Override
    public List<Command> createCommands() {
        return Arrays.asList(
                new TinkerCommand()
        );
    }

    public static class Settings {

    }

    public static class Preferences {

    }

    private static final List<String> CONFUSION = Arrays.asList(
            "UH?",
            "WUT?",
            "WOAH!",
            "NOPE!",
            "NAH!",
            "MMH?"
    );
}
