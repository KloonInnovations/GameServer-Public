package io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolSidebar;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkHandler;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.commands.CylinderItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.menu.CylinderToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.snipe.CylinderSnipe;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.work.CylinderGenSettings;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.work.CylinderWork;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.CylinderTool.*;

public class CylinderTool extends CreativeTool<CylinderToolSettings, Preferences> implements SnipeWorkTool<CylinderToolSettings> {
    public static final String ICON = "⛁";

    public static final Color COLOR = new Color(88, 170, 134);
    public static final TextColor TEXT_COLOR = TextColor.color(COLOR);

    private final SnipeWorkHandler<CylinderToolSettings, CylinderTool> snipeHandler;

    public CylinderTool() {
        super(CreativeToolType.CYLINDER, new ToolDataDef<>(CylinderToolSettings::new, CylinderToolSettings.class, Preferences::new, Preferences.class));
        this.snipeHandler = new SnipeWorkHandler<>("Creating Cylinder", this);
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        snipeHandler.handleUse(player, click);
    }

    @Override
    public BlocksWork createWork(CreativePlayer player, ToolClick click, BlockVec targetPos, CylinderToolSettings settings) {
        CylinderGenSettings genSettings = settings.createGenSettings(targetPos, player.computeMaskLookup());
        return new CylinderWork(player.getInstance(), genSettings);
    }

    @Override
    public ChangeMeta createChangeMeta(CreativePlayer player, ToolClick click, CylinderToolSettings settings) {
        return new ChangeMeta(toolType, STR."<\{TEXT_COLOR.asHexString()}>Cylinder Generation",
                MM."<gray>Generating a cylinder of radius \{settings.getRadius()} and thickness \{settings.getThickness()}",
                SoundEvent.ENTITY_COD_FLOP, 0.64);
    }

    @Override
    public void onJobComplete(CreativePlayer player, ToolClick click, CylinderToolSettings settings, Change change) {
        player.broadcast().send(MsgCat.TOOL,
                TEXT_COLOR, "CYLINDER", MM."<gray>Of radius \{settings.getRadius()} and thickness \{settings.getThickness()}!",
                SoundEvent.ENTITY_COD_FLOP, Pitch.rng(0.6, 0.2));
    }

    @Override
    public void writeUsage(List<Component> lore, CylinderToolSettings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>CREATE CYLINDER");
        lore.addAll(MM_WRAP."<gray>Generates a cylinder centered around your <snipe_target><gray>.");
    }

    @Override
    public @Nullable ToolSidebar<CylinderToolSettings, Preferences> createSidebar() {
        return (player, settings, preferences) -> {
            Lore lore = new Lore();
            lore.add(MM."<white>\{ICON} \{toolType.getDisplayName()}");
            lore.add(MM."<white>Radius: <green>\{(int) settings.getRadius()} <dark_gray>\{settings.isEven() ? "even" : "odd"}");
            lore.add(MM."<white>Thickness: <green>\{(int) settings.getThickness()} blocks");
            List<String> properties = new ArrayList<>();
            if (settings.isHollow()) {
                properties.add("Hollow");
            }
            if (!properties.isEmpty()) {
                lore.add(MM."<dark_gray>\{String.join(", ", properties)}");
            }
            return lore;
        };
    }

    @Override
    public List<Command> createCommands() {
        return List.of(new CylinderItemCommand(this));
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new CylinderToolMenu(this, itemRef).display(player);
    }

    @Override
    public @Nullable ToolSnipe<CylinderToolSettings> createSnipe(CreativePlayer player) {
        return new CylinderSnipe(player, this);
    }

    public static class Preferences {
        private boolean animatePreview = true;

        public boolean isAnimatePreview() {
            return animatePreview;
        }

        public void setAnimatePreview(boolean animatePreview) {
            this.animatePreview = animatePreview;
        }
    }
}
