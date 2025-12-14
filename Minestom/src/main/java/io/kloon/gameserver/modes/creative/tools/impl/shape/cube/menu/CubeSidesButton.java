package io.kloon.gameserver.modes.creative.tools.impl.shape.cube.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeToolSettings;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.work.CubeGenSettings;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CubeSidesButton implements ChestButton {
    private final CubeToolMenu menu;

    public CubeSidesButton(CubeToolMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        if (click.isRightClick()) {
            menu.getTool().editItemBound(player, menu.getItemRef(), settings -> {
                settings.setCuboidButtons(true);
            });
            player.playSound(SoundEvent.ENTITY_AXOLOTL_ATTACK, 1.5);
            menu.reload().display(player);
        } else {
            new SignUXNumberInput().bounds(1, CubeGenSettings.MAX_SIZE).display(player, "Cube size", size -> {
                menu.getTool().editItemBound(player, menu.getItemRef(), settings -> {
                    settings.setSize(size.intValue());

                    player.msg().send(MsgCat.TOOL,
                            NamedTextColor.GREEN, "CUBE SIZE!", MM."<gray>Set all sides to \{NumberFmt.NO_DECIMAL.format(size.intValue())} blocks!",
                            SoundEvent.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON, 0.7);
                    player.closeInventory();
                });
            });
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Cube Size";

        Lore lore = new Lore();
        lore.wrap("<gray>How many blocks each side of the cube is.");
        lore.addEmpty();

        CubeToolSettings itemBound = menu.getItemBound();
        int size = itemBound.getWidth();
        lore.add(MM."<gray>Size: <green>\{NumberFmt.NO_DECIMAL.format(size)} blocks");
        lore.addEmpty();
        lore.add("<rcta>Click for cuboid!");
        lore.add("<lcta>Click to edit size!");

        return MenuStack.of(Material.PUFFERFISH, name, lore);
    }
}
