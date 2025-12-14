package io.kloon.gameserver.modes.creative.tools.impl.layer.menu;

import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility.SnipeVisibilityButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.ItemBoundNumberButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.data.ToolData;
import io.kloon.gameserver.modes.creative.tools.impl.layer.LayerTool;
import io.kloon.gameserver.modes.creative.tools.impl.layer.params.LayerAxis;
import io.kloon.gameserver.modes.creative.tools.impl.layer.params.LayerShape;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.tools.menus.ToolPatternSelectionButton;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class LayerToolMenu extends CreativeToolMenu<LayerTool> {
    public static final NumberInput<LayerTool.Settings> RADIUS =  NumberInput.consumerInt(
            Material.NAUTILUS_SHELL, NamedTextColor.AQUA,
            "Radius",
            MM_WRAP."<gray>How many blocks to transform around each side of your <snipe_target><gray>.",
            LayerTool.Settings.DEFAULT_RADIUS, 1, 384,
            LayerTool.Settings::getRadius, LayerTool.Settings::setRadius);

    public LayerToolMenu(LayerTool tool, ItemRef itemRef) {
        super(tool, itemRef);
    }

    @Override
    protected void registerButtons() {
        reg(10, slot -> new ToolPatternSelectionButton(this, slot));
        reg(12, slot -> new ItemBoundNumberButton<>(slot, this, RADIUS));

        LayerTool.Settings settings = tool.getItemBound(itemRef);

        Cycle<LayerAxis> axisCycle = new Cycle<>(LayerAxis.values());
        axisCycle.select(settings.getAxis());
        reg(14, slot -> new CycleButton<>(axisCycle, slot)
                .withIcon(() -> axisCycle.getSelected().getIcon())
                .withTitle(MM."<title>Axis")
                .withDescription(MM_WRAP."<gray>What axis the normal of the layer lies on.")
                .withOnClick((p, axis) -> {
                    CreativePlayer player = (CreativePlayer) p;
                    tool.editItemBound(player, itemRef, s -> s.setAxis(axis));

                    ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Set layer axis to \{axis.label()}<gray>!",
                            SoundEvent.BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE, 0.65 + axis.ordinal() * 0.05);
                }));

        Cycle<LayerShape> shapeCycle = new Cycle<>(LayerShape.values());
        shapeCycle.select(settings.getShape());
        reg(16, slot -> new CycleButton<>(shapeCycle, slot)
                .withIcon(Material.WARPED_FUNGUS)
                .withTitle(MM."<title>Shape")
                .withDescription(MM_WRAP."<gray>Which shape is generated on the layer.")
                .withOnClick((p, shape) -> {
                    CreativePlayer player = (CreativePlayer) p;
                    tool.editItemBound(player, itemRef, s -> s.setShape(shape));

                    ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Set layer shape to \{shape.label()}<gray>!",
                            SoundEvent.ENTITY_MOOSHROOM_CONVERT, 1.8 + shape.ordinal() * 0.1, 0.5);
                }));

        reg(30, SnipeVisibilityButton::new);
        reg(32, new SnipeSettingsMenu(this));

        reg().toolCommands(this);
    }
}
