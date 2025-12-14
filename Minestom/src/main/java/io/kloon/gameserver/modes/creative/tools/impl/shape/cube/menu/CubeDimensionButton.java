package io.kloon.gameserver.modes.creative.tools.impl.shape.cube.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeToolSettings;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.work.CubeGenSettings;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.coordinates.Axis;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CubeDimensionButton implements ChestButton {
    private final CubeToolMenu menu;
    private final String label;
    private final Axis axis;

    public CubeDimensionButton(CubeToolMenu menu, String label, Axis axis) {
        this.menu = menu;
        this.label = label;
        this.axis = axis;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        new SignUXNumberInput().bounds(1, CubeGenSettings.MAX_SIZE).display(player, label, size -> {
            menu.getTool().editItemBound(player, menu.getItemRef(), settings -> {
                setDimension(settings, size.intValue());

                player.msg().send(MsgCat.TOOL,
                        NamedTextColor.GREEN, "CUBE " + label, MM."<gray>Set \{label.toLowerCase()} to \{NumberFmt.NO_DECIMAL.format(size.intValue())}!",
                        SoundEvent.BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN, 0.65);
                player.closeInventory();
            });
        });
    }

    private void setDimension(CubeToolSettings settings, int size) {
        switch (axis) {
            case X -> settings.setWidth(size);
            case Y -> settings.setHeight(size);
            case Z -> settings.setDepth(size);
        }
    }

    private int getDimension(CubeToolSettings settings) {
        return switch (axis) {
            case X -> settings.getWidth();
            case Y -> settings.getHeight();
            case Z -> settings.getDepth();
        };
    }

    public static HeadProfile getHead(Axis axis) {
        return switch (axis) {
            case X -> SkinCache.toHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhjYmRiOTk4ZmI0ZDVlMjg5NjllMjNmNGJiODdjOWRlNWMzMDNmYmViOTBhOTRkOTFkNzQ0NWE2NzNlYTU5ZCJ9fX0=");
            case Y -> SkinCache.toHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM5MTFjZTY3ZDE1NWVmYTZiZTEyMzNlNDZkMjUxOWJlYTJmYThkNDU1MDIyMzMyYTUwNzM4MDA1NGQwYTI2OSJ9fX0=");
            case Z -> SkinCache.toHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ1YTI0YjU0YjdjY2EzYjlmOTliMzM3ZWEwZDExMjliYzYxZmY2YWE4ZGYxNzc4MDY4NmQ4Y2M0OGY1NWYyNCJ9fX0=");
        };
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Cube \{label}";

        Lore lore = new Lore();
        lore.wrap(MM."<gray>How many blocks for the \{label.toLowerCase()} of the cube.");
        lore.addEmpty();

        CubeToolSettings settings = menu.getItemBound();
        int size = getDimension(settings);
        lore.add(MM."<gray>\{label}: <green>\{NumberFmt.NO_DECIMAL.format(size)} blocks");

        lore.addEmpty();
        lore.add("<cta>Click to edit!");

        HeadProfile icon = getHead(axis);
        return MenuStack.ofHead(icon).name(name).lore(lore).build();
    }
}
