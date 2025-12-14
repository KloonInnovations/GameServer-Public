package io.kloon.gameserver.modes.creative.tools.impl.erosion;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.menu.preferences.common.snipeshape.SnipeShapePref;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkHandler;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkTool;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.commands.ErosionItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.menu.ErosionToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.params.ErosionPreset;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.work.ErosionGen;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.work.ErosionWork;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.prebuilt.BasicShapeSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.shape.SnipeShape;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.erosion.ErosionTool.*;

public class ErosionTool extends CreativeTool<ErosionToolSettings, Preferences> implements SnipeWorkTool<ErosionToolSettings> {
    private final SnipeWorkHandler<ErosionToolSettings, ErosionTool> snipeHandler;

    public ErosionTool() {
        super(CreativeToolType.EROSION, new ToolDataDef<>(ErosionToolSettings::new, ErosionToolSettings.class, Preferences::new, Preferences.class));
        this.snipeHandler = new SnipeWorkHandler<>("Eroding", this);
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        snipeHandler.handleUse(player, click);
    }

    @Override
    public BlocksWork createWork(CreativePlayer player, ToolClick click, BlockVec targetPos, ErosionToolSettings settings) {
        boolean sphere = getPlayerBound(player).getSnipeShape() == SnipeShape.SPHERE;
        ErosionGen genSettings = settings.createGenSettings(targetPos, sphere, click.isLeftClick(), player.computeMaskLookup());
        return new ErosionWork(player.getInstance(), genSettings);
    }

    @Override
    public ChangeMeta createChangeMeta(CreativePlayer player, ToolClick click, ErosionToolSettings settings) {
        ErosionPreset preset = settings.getParams().getPreset();
        int radius = settings.getRadius();
        Component description;
        if (preset == null) {
            description = MM."<gray>Custom erosion with radius \{radius}.";
        } else {
            description = MM."<gray>\{preset.getName()} with radius \{radius}.";
        }

        String titleMm = click.isRightClick() ? "<white>Erosion" : "<white>Anti-Erode";

        return new ChangeMeta(toolType, titleMm, description,
                SoundEvent.ENTITY_ARMADILLO_BRUSH, 1.4);
    }

    @Override
    public void onJobComplete(CreativePlayer player, ToolClick click, ErosionToolSettings settings, Change change) {
        SnipeShape shape = getPlayerBound(player).getSnipeShape();
        int radius = settings.getRadius();
        ErosionPreset preset = settings.getParams().getPreset();
        if (preset == null) {
            player.broadcast().send(MsgCat.TOOL, NamedTextColor.WHITE, "EROSION!", MM."<gray>Applied custom erosion over \{radius} radius \{shape.label()}!");
        } else {
            player.broadcast().send(MsgCat.TOOL, NamedTextColor.WHITE, "EROSION!", MM."<gray>\{preset.getName()} over \{radius} radius \{shape.label()}!");
        }
        player.playSound(SoundEvent.ENTITY_ARMADILLO_BRUSH, Pitch.rng(1.1, 0.9));
    }

    @Override
    public @Nullable ToolSnipe<ErosionToolSettings> createSnipe(CreativePlayer player) {
        return new BasicShapeSnipe<>(player, this, Material.WHITE_STAINED_GLASS, new Color(246, 246, 246));
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new ErosionToolMenu(player, this, itemRef).display(player);
    }

    @Override
    public Component renderName(ErosionToolSettings settings) {
        ErosionPreset preset = settings.getParams().getPreset();
        String presetName = preset == null ? "Custom" : preset.getName();
        return MM."<white>\{toolType.getDisplayName()} (<aqua>\{presetName}<white>)";
    }

    @Override
    public List<Command> createCommands() {
        return List.of(new ErosionItemCommand(this));
    }

    @Override
    public void writeUsage(List<Component> lore, ErosionToolSettings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>ERODE");
        lore.addAll(MM_WRAP."<gray>Applies erosion around your <snipe_target><gray>.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>REVERSE");
        lore.addAll(MM_WRAP."<gray>Flips your erosion settings and applies the reverse operation on your <snipe_target><gray>.");
    }

    public static class Preferences implements SnipeShapePref {
        private String snipeShape;

        @Override
        public SnipeShape getSnipeShape() {
            return SnipeShape.BY_DB_KEY.get(snipeShape, SnipeShape.SPHERE);
        }

        @Override
        public void setSnipeShape(SnipeShape shape) {
            this.snipeShape = shape.getDbKey();
        }
    }
}
