package io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid;

import io.kloon.gameserver.chestmenus.util.Lore;
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
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.commands.PyramidItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.menu.PyramidToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.snipe.PyramidSnipe;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.work.PyramidGenSettings;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.work.PyramidWork;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class PyramidTool extends CreativeTool<PyramidToolSettings, PyramidTool.Preferences> implements SnipeWorkTool<PyramidToolSettings> {
    public static final String ICON = "⃤";

    private final SnipeWorkHandler<PyramidToolSettings, PyramidTool> snipeHandler;

    public PyramidTool() {
        super(CreativeToolType.PYRAMID, new ToolDataDef<>(PyramidToolSettings::new, PyramidToolSettings.class, Preferences::new, Preferences.class));
        this.snipeHandler = new SnipeWorkHandler<>("Creating Pyramid", this);
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        snipeHandler.handleUse(player, click);
    }

    @Override
    public BlocksWork createWork(CreativePlayer player, ToolClick click, BlockVec targetPos, PyramidToolSettings settings) {
        PyramidGenSettings genSettings = settings.createGenSettings(targetPos, player.computeMaskLookup());
        return new PyramidWork(player.getInstance(), genSettings);
    }

    @Override
    public ChangeMeta createChangeMeta(CreativePlayer player, ToolClick click, PyramidToolSettings settings) {
        return new ChangeMeta(toolType, "<yellow>Pyramid Generation",
                MM."<gray>Generating a pyramid.",
                SoundEvent.ENTITY_LLAMA_SWAG, 1.9);
    }

    @Override
    public void onJobComplete(CreativePlayer player, ToolClick click, PyramidToolSettings settings, Change change) {
        if (ThreadLocalRandom.current().nextInt(9) == 0) {
            player.msg().send(MsgCat.TOOL,
                    NamedTextColor.YELLOW, "PYRAMID!", MM."<gray>It's uh... a pyramid!",
                    SoundEvent.ENTITY_LLAMA_SWAG, 1.9);
        } else {
            player.msg().send(MsgCat.TOOL,
                    NamedTextColor.YELLOW, "PYRAMID!", MM."<gray>Of \{settings.getSteps()} steps!",
                    SoundEvent.ENTITY_LLAMA_SWAG, 1.9);
        }
    }

    @Override
    public @Nullable ToolSidebar<PyramidToolSettings, Preferences> createSidebar() {
        return (player, settings, preferences) -> {
            Lore lore = new Lore();
            lore.add(MM."<white>\{ICON} \{toolType.getDisplayName()}");
            int ln = settings.getStepLength();
            int height = settings.getStepHeight();
            lore.add(MM."<white>Steps: <yellow>\{settings.getSteps()}<white>, <green>\{ln}x\{height}x\{ln}");
            List<String> properties = new ArrayList<>();
            if (settings.isHollow()) {
                properties.add("Hollow");
            }
            if (settings.isUpsideDown()) {
                properties.add("Upside-down");
            }
            if (!properties.isEmpty()) {
                lore.add(MM."<dark_gray>\{String.join(", ", properties)}");
            }
            return lore;
        };
    }

    @Override
    public List<Command> createCommands() {
        return List.of(new PyramidItemCommand(this));
    }

    @Override
    public void writeUsage(List<Component> lore, PyramidToolSettings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>CREATE PYRAMID");
        lore.addAll(MM_WRAP."<gray>Generates a pyramid with its bottom-center at your <snipe_target><gray>.");
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new PyramidToolMenu(this, itemRef).display(player);
    }

    @Override
    public @Nullable ToolSnipe<PyramidToolSettings> createSnipe(CreativePlayer player) {
        return new PyramidSnipe(player, this);
    }

    public static class Preferences {

    }
}
