package io.kloon.gameserver.modes.creative.tools.impl.shape.cube;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility.SnipeVisibilityPref;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolSidebar;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkHandler;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.commands.CubeItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.menu.CubeToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.snipe.CubeSnipe;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.work.CubeGenSettings;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.work.CubeWork;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeTool.Preferences;

public class CubeTool extends CreativeTool<CubeToolSettings, Preferences> implements SnipeWorkTool<CubeToolSettings> {
    public static final String ICON = "⬜";

    private final SnipeWorkHandler<CubeToolSettings, CubeTool> snipeHandler;

    public CubeTool() {
        super(CreativeToolType.CUBE, new ToolDataDef<>(CubeToolSettings::new, CubeToolSettings.class, Preferences::new, Preferences.class));
        this.snipeHandler = new SnipeWorkHandler<>("Creating Cuboid", this);
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        snipeHandler.handleUse(player, click);
    }

    @Override
    public BlocksWork createWork(CreativePlayer player, ToolClick click, BlockVec targetPos, CubeToolSettings settings) {
        CubeGenSettings genSettings = settings.createGenSettings(targetPos, player.computeMaskLookup());
        return new CubeWork(player.getInstance(), genSettings);
    }

    @Override
    public ChangeMeta createChangeMeta(CreativePlayer player, ToolClick click, CubeToolSettings settings) {
        Vec dimensions = settings.getDimensions();
        boolean cube = settings.isCube();
        Component description = cube
                ? MM."<gray>Generating a cube with sides of \{NumberFmt.NO_DECIMAL.format(dimensions.x())}."
                : MM."<gray>Generating a \{BoundingBoxUtils.fmtDimensions(dimensions)} cuboid.";
        return new ChangeMeta(toolType, "<#FFAA00>Cuboid Generation",
                description,
                SoundEvent.ITEM_ARMOR_EQUIP_TURTLE, 0.5);
    }

    @Override
    public void onJobComplete(CreativePlayer player, ToolClick click, CubeToolSettings settings, Change change) {
        Vec dimensions = settings.getDimensions();
        boolean cube = settings.isCube();
        String subject = cube ? "CUBE" : "CUBOID";
        Component details = cube
                ? MM."<gray>With sides of length \{NumberFmt.NO_DECIMAL.format(dimensions.x())}!"
                : MM."<gray>With size of \{BoundingBoxUtils.fmtDimensions(dimensions)}!";

        player.broadcast().send(MsgCat.TOOL,
                NamedTextColor.GOLD, subject, details,
                SoundEvent.ITEM_ARMOR_EQUIP_TURTLE, 0.5);
    }

    @Override
    public void writeUsage(List<Component> lore, CubeToolSettings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>CREATE CUBOID");
        lore.addAll(MM_WRAP."<gray>Generates a cuboid centered on your <snipe_target><gray>.");
    }

    @Override
    public @Nullable ToolSidebar<CubeToolSettings, Preferences> createSidebar() {
        return (player, settings, preferences) -> {
            Lore lore = new Lore();
            lore.add(MM."<white>\{ICON} \{toolType.getDisplayName()}");
            if (settings.hasCuboidButtons()) {
                lore.add(MM."<white>Size: <gold>\{settings.getWidth()} blocks");
            } else {
                lore.add(MM."<white>Size: <gold>\{BoundingBoxUtils.fmtDimensions(settings.getDimensions())} blocks");
            }
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
        return List.of(new CubeItemCommand(this));
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new CubeToolMenu(this, itemRef).display(player);
    }

    @Override
    public @Nullable ToolSnipe<CubeToolSettings> createSnipe(CreativePlayer player) {
        return new CubeSnipe(player, this);
    }

    public static class Preferences implements SnipeVisibilityPref {
        private String snipeVisibility;

        @Override
        public SnipeVisibility getSnipeVisibility() {
            return SnipeVisibility.BY_DB_KEY.get(snipeVisibility, SnipeVisibility.GLOWING);
        }

        @Override
        public void setSnipeVisibility(SnipeVisibility visibility) {
            this.snipeVisibility = visibility.getDbKey();
        }
    }
}
