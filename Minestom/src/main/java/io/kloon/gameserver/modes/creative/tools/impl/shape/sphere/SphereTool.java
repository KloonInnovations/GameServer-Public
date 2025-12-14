package io.kloon.gameserver.modes.creative.tools.impl.shape.sphere;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolSidebar;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.ToolSoundCd;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkHandler;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.commands.SphereItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.snipe.SphereSnipe;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.work.SphereGenSettings;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.work.SphereWork;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.menu.SphereToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.SphereTool.*;

public class SphereTool extends CreativeTool<SphereToolSettings, Preferences> implements SnipeWorkTool<SphereToolSettings> {
    public static final String ICON = "⃝";
    public static final int DEFAULT_RADIUS = 4;

    private final SnipeWorkHandler<SphereToolSettings, SphereTool> snipeHandler;
    private final ToolSoundCd soundCd = new ToolSoundCd();

    public SphereTool() {
        super(CreativeToolType.SPHERE, new ToolDataDef<>(SphereToolSettings::new, SphereToolSettings.class, Preferences::new, Preferences.class));
        this.snipeHandler = new SnipeWorkHandler<>("Creating Sphere", this);
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        snipeHandler.handleUse(player, click);
    }

    @Override
    public BlocksWork createWork(CreativePlayer player, ToolClick click, BlockVec targetPos, SphereToolSettings settings) {
        MaskLookup maskLookup = player.computeMaskLookup();
        SphereGenSettings genSettings = settings.createGenSettings(targetPos, maskLookup);
        return new SphereWork(player.getInstance(), genSettings);
    }

    @Override
    public ChangeMeta createChangeMeta(CreativePlayer player, ToolClick click, SphereToolSettings settings) {
        return new ChangeMeta(toolType, "<aqua>Sphere Generation",
                MM."<gray>Generating a sphere of radius \{NumberFmt.ONE_DECIMAL.format(settings.getRadius())}.",
                SoundEvent.ENTITY_WARDEN_LISTENING, 1.8);
    }

    @Override
    public void onJobComplete(CreativePlayer player, ToolClick click, SphereToolSettings settings, Change change) {
        player.msg().send(MsgCat.TOOL,
                NamedTextColor.AQUA, "SPHERE!", MM."<gray>With radius of \{NumberFmt.NO_DECIMAL.format(settings.getRadius())}!");
        soundCd.play(player, click, SoundEvent.ENTITY_WARDEN_LISTENING, 1.8);
    }

    @Override
    public void writeUsage(List<Component> lore, SphereToolSettings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>CREATE SPHERE");
        lore.addAll(MM_WRAP."<gray>Generates a sphere centered around your <snipe_target><gray>.");
    }

    @Override
    public @Nullable ToolSidebar<SphereToolSettings, Preferences> createSidebar() {
        return (player, settings, preferences) -> {
            Lore lore = new Lore();
            lore.add(MM."<white>\{ICON} \{toolType.getDisplayName()}");
            lore.add(MM."<white>Radius: <aqua>\{settings.getRadius()} blocks <dark_gray>\{settings.isCentered() ? "odd" : "even"}");
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
        return List.of(new SphereItemCommand(this));
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new SphereToolMenu(this, itemRef).display(player);
    }

    @Override
    public @Nullable ToolSnipe<SphereToolSettings> createSnipe(CreativePlayer player) {
        return new SphereSnipe(player, this);
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
