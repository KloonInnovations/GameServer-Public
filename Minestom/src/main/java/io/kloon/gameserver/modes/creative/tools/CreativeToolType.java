package io.kloon.gameserver.modes.creative.tools;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.kloon.gameserver.modes.creative.commands.tools.MenuCommand;
import io.kloon.gameserver.modes.creative.tools.impl.MainMenuTool;
import io.kloon.gameserver.modes.creative.tools.impl.move.MoveTool;
import io.kloon.gameserver.modes.creative.tools.impl.selection.expander.SelectionExpanderTool;
import io.kloon.gameserver.modes.creative.tools.impl.blend.BlendTool;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.ErosionTool;
import io.kloon.gameserver.modes.creative.tools.impl.fill.FillTool;
import io.kloon.gameserver.modes.creative.tools.impl.history.HistoryTool;
import io.kloon.gameserver.modes.creative.tools.impl.laser.LaserTool;
import io.kloon.gameserver.modes.creative.tools.impl.layer.LayerTool;
import io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.SelectionPusherTool;
import io.kloon.gameserver.modes.creative.tools.impl.replace.ReplaceTool;
import io.kloon.gameserver.modes.creative.tools.impl.selection.regular.SelectionTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.CylinderTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.PyramidTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.SphereTool;
import io.kloon.gameserver.modes.creative.tools.impl.stack.StackTool;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.TeleportTool;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkerTool;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.WaypointsTool;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.Supplier;

public enum CreativeToolType {
    MENU("menu", MainMenuTool::new, "Main Menu", Material.NETHER_STAR, CreativeToolCategory.ESSENTIAL, true),
    HISTORY("history", HistoryTool::new, "History", Material.ECHO_SHARD, CreativeToolCategory.ESSENTIAL, false),

    CLEAR("clear", null, "Clear", Material.SCULK_SENSOR, CreativeToolCategory.ESSENTIAL, true),

    WEATHER("weather", null, "Weather", Material.WATER_BUCKET, CreativeToolCategory.SYSTEM, false),
    TIME("time", null, "Time", Material.CLOCK, CreativeToolCategory.SYSTEM, false),

    SELECTION("selection", SelectionTool::new, "Selection Tool", Material.LEAD, CreativeToolCategory.SELECTION, false),
    SELECTION_PUSHER("selection_pusher", SelectionPusherTool::new, "Selection Pusher", Material.PISTON, CreativeToolCategory.SELECTION, false),
    SELECTION_EXPANDER("sex", SelectionExpanderTool::new, "Selection Expander", Material.MUSIC_DISC_5, CreativeToolCategory.SELECTION, false),

    TINKER("tinker", TinkerTool::new, "Tinker Tool", Material.FEATHER, CreativeToolCategory.GENERAL, false),
    FILL("fill", FillTool::new, "Fill Tool", Material.BUCKET, CreativeToolCategory.GENERAL, false),
    REPLACE("replace", ReplaceTool::new, "Replace Tool", Material.MUSIC_DISC_PRECIPICE, CreativeToolCategory.GENERAL, false),
    COPY_PASTE("copy_paste", CopyPasteTool::new, "Copy/Paste Tool", Material.SHEARS, CreativeToolCategory.GENERAL, false),
    MOVE("move", MoveTool::new, "Move Tool", Material.SHULKER_SHELL, CreativeToolCategory.GENERAL, false),
    STACK("stack", StackTool::new, "Stack Tool", Material.COPPER_GRATE, CreativeToolCategory.GENERAL, false),
    LAYER("layer", LayerTool::new, "Layer Tool", Material.NETHERITE_SHOVEL, CreativeToolCategory.GENERAL, false),

    LASER("laser", LaserTool::new, "Laser Tool", Material.BLAZE_ROD, CreativeToolCategory.GENERAL, false),

    SPHERE("sphere", SphereTool::new, "Sphere Tool", Material.HEART_OF_THE_SEA, CreativeToolCategory.SHAPE, false),
    CUBE("cube", CubeTool::new, "Cube Tool", Material.COPPER_INGOT, CreativeToolCategory.SHAPE, false),
    PYRAMID("pyramid", PyramidTool::new, "Pyramid Tool", Material.GLOWSTONE_DUST, CreativeToolCategory.SHAPE, false),
    CYLINDER("cylinder", CylinderTool::new, "Cylinder Tool", Material.MUSIC_DISC_CREATOR, CreativeToolCategory.SHAPE, false),

    EROSION("erosion", ErosionTool::new, "Erosion Tool", Material.WHITE_DYE, CreativeToolCategory.TERRAFORMING, false),
    BLEND("blend", BlendTool::new, "Blend Tool", Material.DROWNED_SPAWN_EGG, CreativeToolCategory.TERRAFORMING, false),

    TELEPORTER("teleporter", TeleportTool::new, "Teleporter", Material.ENDER_EYE, CreativeToolCategory.MOVEMENT, true),
    WAYPOINTS("waypoints", WaypointsTool::new, "Waypoints Tool", Material.BLUE_BANNER, CreativeToolCategory.MOVEMENT, false),

    HAND("hand", null, "Hand", Material.IRON_SHOVEL, CreativeToolCategory.SYSTEM, false),
    UNKNOWN("unknown", null, "Unknown Tool", Material.STICK, CreativeToolCategory.SYSTEM, false),
    ;

    private final String dbKey;
    private final Supplier<CreativeTool<?, ?>> constructor;
    private final String displayName;
    private final Material material;
    private final CreativeToolCategory category;
    private final boolean availableWithoutBuildPerms;

    CreativeToolType(String dbKey, @Nullable Supplier<CreativeTool<?, ?>> constructor, String displayName, Material material, CreativeToolCategory category, boolean availableWithoutBuildPerms) {
        this.dbKey = dbKey;
        this.constructor = constructor;
        this.displayName = displayName;
        this.material = material;
        this.category = category;
        this.availableWithoutBuildPerms = availableWithoutBuildPerms;
    }

    public String getDbKey() {
        return dbKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    @Nullable
    public String getCommandOverride() {
        if (this == MENU) {
            return MenuCommand.LABEL;
        }
        return null;
    }

    public boolean canInstantiate() {
        return constructor != null;
    }

    @Nullable
    public CreativeTool<?, ?> instantiate() {
        if (constructor == null) return null;
        return constructor.get();
    }

    public String getCommandLabel() {
        return switch (this) {
            case SELECTION_PUSHER -> "push";
            case SELECTION_EXPANDER -> "sex";
            default -> dbKey;
        };
    }

    public CreativeToolCategory getCategory() {
        return category;
    }

    public boolean isAvailableWithoutBuildPerms() {
        return availableWithoutBuildPerms;
    }

    public static final EnumQuery<String, CreativeToolType> BY_DBKEY = new EnumQuery<>(values(), CreativeToolType::getDbKey);

    public static final StdSerializer<CreativeToolType> JACKSON_SERIALIZER = new StdSerializer<>(CreativeToolType.class) {
        public void serialize(CreativeToolType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.dbKey);
        }
    };

    public static final StdDeserializer<CreativeToolType> JACKSON_DESERIALIZER = new StdDeserializer<>(CreativeToolType.class) {
        public CreativeToolType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            String dbKey = p.getValueAsString();
            return BY_DBKEY.get(dbKey, UNKNOWN);
        }
    };
}
