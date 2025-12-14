package io.kloon.gameserver.modes.creative.tools.impl.layer;

import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.menu.preferences.common.radius.RadiusSettings;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ItemBoundPattern;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.ToolSoundCd;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkHandler;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkTool;
import io.kloon.gameserver.modes.creative.tools.impl.layer.commands.LayerItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.layer.menu.LayerToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.layer.params.LayerAxis;
import io.kloon.gameserver.modes.creative.tools.impl.layer.params.LayerShape;
import io.kloon.gameserver.modes.creative.tools.impl.layer.work.LayerChange;
import io.kloon.gameserver.modes.creative.tools.impl.layer.work.LayerGen;
import io.kloon.gameserver.modes.creative.tools.impl.layer.work.LayerWork;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.prebuilt.BasicCuboidSnipe;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.coordinates.Axis;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.layer.LayerTool.*;

public class LayerTool extends CreativeTool<Settings, Preferences> implements SnipeWorkTool<Settings> {
    private final SnipeWorkHandler<Settings, LayerTool> snipeHandler;
    private final ToolSoundCd soundCd = new ToolSoundCd();

    public static final TextColor COLOR = TextColor.color(219, 166, 185);

    public LayerTool() {
        super(CreativeToolType.LAYER, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
        this.snipeHandler = new SnipeWorkHandler<>("Layering", this);
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        snipeHandler.handleUse(player, click);
    }

    @Override
    public BlocksWork createWork(CreativePlayer player, ToolClick click, BlockVec targetPos, Settings settings) {
        MaskLookup mask = player.computeMaskLookup();
        CreativePattern pattern = settings.getPattern();
        Axis axis = settings.getAxis().computeAxis(player);
        LayerGen layerGen = new LayerGen(targetPos, mask, pattern, settings.getRadius(), axis, settings.getShape());
        return new LayerWork(player.getInstance(), layerGen);
    }

    @Override
    public ChangeMeta createChangeMeta(CreativePlayer player, ToolClick click, Settings settings) {
        int radius = settings.getRadius();
        Axis axis = settings.getAxis().computeAxis(player);
        return new ChangeMeta(toolType, STR."<\{COLOR.asHexString()}>Layering",
                MM."<gray>Editing a layer on \{axis.name()} axis of \{radius} radius.",
                SoundEvent.BLOCK_BEEHIVE_EXIT, 1.5);
    }

    @Override
    public void onJobComplete(CreativePlayer player, ToolClick click, Settings settings, Change change) {
        LayerChange layerChange = (LayerChange) change;

        int modified = layerChange.getAfter().count();
        String modifiedFmt = NumberFmt.NO_DECIMAL.format(modified);

        Axis axis = layerChange.getAxis();

        if (modified == 1) {
            player.broadcast().send(MsgCat.TOOL, COLOR, "LAYER!", MM."<gray>Radius of \{NumberFmt.NO_DECIMAL.format(settings.getRadius())} on \{axis.name()} axis changed \{modifiedFmt} block!");
        } else {
            player.broadcast().send(MsgCat.TOOL, COLOR, "LAYER!", MM."<gray>Radius of \{NumberFmt.NO_DECIMAL.format(settings.getRadius())} on \{axis.name()} axis changed \{modifiedFmt} blocks!");
        }
        soundCd.play(player, click, SoundEvent.BLOCK_BEEHIVE_EXIT, Pitch.rng(1.5, 0.1));
    }

    @Override
    public @Nullable ToolSnipe<Settings> createSnipe(CreativePlayer player) {
        return new BasicCuboidSnipe<>(player, Material.GRAY_STAINED_GLASS, COLOR, settings -> {
            BlockVec target = player.getSnipe().computeTarget();
            Axis axis = settings.getAxis().computeAxis(player);
            return LayerGen.computeBoundingBox(target, settings.getRadius(), axis);
        });
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.CLICK_GREEN} <#FF266E><b>BIG TIME LAYER");
        lore.addAll(MM_WRAP."<gray>Places a layer of blocks along an axis.");
    }

    @Override
    public List<Command> createCommands() {
        return List.of(new LayerItemCommand(this));
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new LayerToolMenu(this, itemRef).display(player);
    }

    public static class Settings implements ItemBoundPattern, RadiusSettings {
        public static final int DEFAULT_RADIUS = 4;

        private byte[] pattern;
        private String axis;
        private String shape;
        private int radius = DEFAULT_RADIUS;

        @Override
        public boolean hasPattern() {
            return pattern != null;
        }

        @Override
        public CreativePattern getPattern() {
            if (pattern == null) return new SingleBlockPattern(Block.STONE);
            return MinecraftInputStream.fromBytesSneaky(pattern, CreativePattern.CODEC);
        }

        @Override
        public void setPattern(CreativePattern pattern) {
            this.pattern = MinecraftOutputStream.toBytesSneaky(pattern, CreativePattern.CODEC);
        }

        public LayerAxis getAxis() {
            return LayerAxis.BY_DB_KEY.get(axis, LayerAxis.AUTO);
        }

        public void setAxis(LayerAxis axis) {
            this.axis = axis == null ? null : axis.getDbKey();
        }

        public LayerShape getShape() {
            return LayerShape.BY_DB_KEY.get(shape, LayerShape.SQUARE);
        }

        public void setShape(LayerShape shape) {
            this.shape = shape.getDbKey();
        }

        @Override
        public int getRadius() {
            return radius;
        }

        @Override
        public void setRadius(int radius) {
            this.radius = radius;
        }
    }

    public static class Preferences {

    }
}
