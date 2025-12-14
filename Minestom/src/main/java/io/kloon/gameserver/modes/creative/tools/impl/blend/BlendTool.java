package io.kloon.gameserver.modes.creative.tools.impl.blend;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.menu.preferences.common.radius.RadiusSettings;
import io.kloon.gameserver.modes.creative.menu.preferences.common.snipeshape.SnipeShapePref;
import io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility.SnipeVisibilityPref;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkHandler;
import io.kloon.gameserver.modes.creative.tools.generics.snipe.SnipeWorkTool;
import io.kloon.gameserver.modes.creative.tools.impl.blend.menu.BlendToolMenu;
import io.kloon.gameserver.modes.creative.tools.impl.blend.work.BlendGen;
import io.kloon.gameserver.modes.creative.tools.impl.blend.work.BlendWork;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.prebuilt.BasicShapeSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;
import io.kloon.gameserver.modes.creative.tools.snipe.shape.SnipeShape;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.blend.BlendTool.*;

public class BlendTool extends CreativeTool<Settings, Preferences> implements SnipeWorkTool<Settings> {
    private final SnipeWorkHandler<Settings, BlendTool> snipeHandler;

    public BlendTool() {
        super(CreativeToolType.BLEND, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
        this.snipeHandler = new SnipeWorkHandler<>("Blending", this);
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        snipeHandler.handleUse(player, click);
    }

    @Override
    public BlocksWork createWork(CreativePlayer player, ToolClick click, BlockVec targetPos, Settings settings) {
        Preferences pref = getPlayerBound(player);
        boolean sphere = pref.getSnipeShape() == SnipeShape.SPHERE;
        int radius = settings.getRadius();
        BlendSampling sampling = settings.getSampling();
        boolean doNotSampleAir = click.isLeftClick();
        boolean doNotSampleLiquid = !settings.sampleLiquids;
        boolean doNotChangeOnTies = !settings.changeOnTies;
        MaskLookup mask = player.computeMaskLookup();
        BlendGen blendGen = new BlendGen(targetPos, mask, radius, sampling, sphere, doNotSampleAir, doNotSampleLiquid, doNotChangeOnTies);
        return new BlendWork(player.getInstance(), blendGen);
    }

    @Override
    public ChangeMeta createChangeMeta(CreativePlayer player, ToolClick click, Settings settings) {
        int radius = settings.getRadius();
        return new ChangeMeta(toolType,
                "<light_purple>Blending Blocks",
                MM."<gray>Blending blocks in a radius of \{radius}.",
                SoundEvent.ENTITY_ENDERMITE_AMBIENT, 0.5);
    }

    @Override
    public void onJobComplete(CreativePlayer player, ToolClick click, Settings settings, Change change) {
        int radius = settings.getRadius();
        player.broadcast().send(MsgCat.TOOL,
                NamedTextColor.LIGHT_PURPLE, "BLEND!", MM."<gray>Blended blocks in a radius of \{radius}!",
                SoundEvent.ENTITY_ENDERMITE_AMBIENT, Pitch.rng(0.5, 0.3));
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>BLEND");
        lore.addAll(MM_WRAP."<gray>Blends blocks around your <snipe_target><gray>, turning each block into the most common block around it.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>BLEND WITHOUT AIR");
        lore.addAll(MM_WRAP."<gray>Same as blend, but air won't be sampled when checking nearby blocks.");
    }

    @Override
    public @Nullable ToolSnipe<Settings> createSnipe(CreativePlayer player) {
        return new BasicShapeSnipe<>(player, this, Material.PINK_STAINED_GLASS, new Color(247, 0, 226));
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new BlendToolMenu(this, itemRef, player).display(player);
    }

    public static class Settings implements RadiusSettings {
        public static final int DEFAULT_RADIUS = 4;

        private int radius = DEFAULT_RADIUS;
        private String sampling;
        private boolean sampleLiquids = false;
        private boolean changeOnTies = true;

        @Override
        public int getRadius() {
            return radius;
        }

        @Override
        public void setRadius(int radius) {
            this.radius = radius;
        }

        public BlendSampling getSampling() {
            return BlendSampling.BY_DB_KEY.get(sampling, BlendSampling.CARDINAL);
        }

        public void setSampling(BlendSampling sampling) {
            this.sampling = sampling.getDbKey();
        }

        public boolean isSampleLiquids() {
            return sampleLiquids;
        }

        public void setSampleLiquids(boolean sampleLiquids) {
            this.sampleLiquids = sampleLiquids;
        }

        public boolean isChangeOnTies() {
            return changeOnTies;
        }

        public void setChangeOnTies(boolean changeOnTies) {
            this.changeOnTies = changeOnTies;
        }
    }

    public static class Preferences implements SnipeShapePref, SnipeVisibilityPref {
        private String snipeShape;
        private String snipeVisibility;

        public SnipeShape getSnipeShape() {
            return SnipeShape.BY_DB_KEY.get(snipeShape, SnipeShape.SPHERE);
        }

        public void setSnipeShape(SnipeShape snipeShape) {
            this.snipeShape = snipeShape.getDbKey();
        }

        public SnipeVisibility getSnipeVisibility() {
            return SnipeVisibility.BY_DB_KEY.get(snipeVisibility, SnipeVisibility.GLOWING);
        }

        public void setSnipeVisibility(SnipeVisibility visibility) {
            this.snipeVisibility = visibility.getDbKey();
        }
    }
}
