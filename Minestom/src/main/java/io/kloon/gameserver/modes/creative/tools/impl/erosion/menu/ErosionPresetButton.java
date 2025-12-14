package io.kloon.gameserver.modes.creative.tools.impl.erosion.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.ErosionTool;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.ErosionToolSettings;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.params.ErosionParams;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.params.ErosionPreset;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ErosionPresetButton implements ChestButton {
    private final ErosionToolMenu menu;
    private final ErosionPreset preset;

    public ErosionPresetButton(ErosionToolMenu menu, ErosionPreset preset) {
        this.menu = menu;
        this.preset = preset;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        ErosionTool tool = menu.getTool();
        ItemRef itemRef = menu.getItemRef();
        tool.editItemBound(player, itemRef, settings -> settings.setParams(preset.getErosionParams()));

        player.msg().send(MsgCat.TOOL,
                NamedTextColor.WHITE, "PRESET!", MM."<gray>Set erosion settings to preset \"\{preset.getName()}<gray>\"!",
                SoundEvent.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.3 + 0.1 * preset.ordinal());
        player.closeInventory();
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>\{preset.getName()}";

        ErosionParams params = preset.getErosionParams();
        boolean selected = preset.matches(menu.getSettings().getParams());

        Lore lore = new Lore();
        lore.add("<dark_gray>Erosion Preset");
        lore.addEmpty();
        lore.wrap(MM."<gray>\{preset.getDescription()}");
        lore.addEmpty();
        lore.add(MM."<dark_gray>▪ <aqua>\{params.erosionFaces()} <gray>Erosion Faces");
        lore.add(MM."<dark_gray>▪ <aqua>\{params.erosionIterations()} <gray>Erosion Iterations");
        lore.add(MM."<dark_gray>▪ <aqua>\{params.fillFaces()} <gray>Fill Faces");
        lore.add(MM."<dark_gray>▪ <aqua>\{params.fillIterations()} <gray>Fill Iterations");
        lore.addEmpty();
        if (selected) {
            lore.add("<green>Preset is selected!");
        } else {
            lore.add("<cta>Click to select!");
        }

        return MenuStack.of(preset.getIcon()).name(name).glowing(selected).lore(lore).build();
    }
}
