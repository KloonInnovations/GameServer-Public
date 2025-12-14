package io.kloon.gameserver.modes.creative.masks.menu.material;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.armor.ArmorFamily;
import io.kloon.gameserver.minestom.scheduler.Repeat;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MaskItemTypeButton implements ChestButton {
    private final EditMaskItemMenu menu;
    private final Material material;

    public MaskItemTypeButton(EditMaskItemMenu menu, Material material) {
        this.menu = menu;
        this.material = material;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        boolean selected = material == menu.getMaskItem().getMaterial();
        if (selected) {
            return;
        }

        CreativePlayer player = (CreativePlayer) p;
        MaskItem edited = menu.getMaskItem().withMaterial(material);
        menu.updateMaskAndDisplay(player, edited);

        ArmorFamily armorFamily = ArmorFamily.get(material);
        Repeat.n(player.scheduler(), 3, 7, t -> player.playSound(armorFamily.equipSound(), 0.5 + 0.4 * t));
        player.sendPit(NamedTextColor.GREEN, "MASK PIECE!", MM."<gray>Changed to \{BlockFmt.getName(material)}!");
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>\{BlockFmt.getName(material)}";

        boolean selected = material == menu.getMaskItem().getMaterial();

        Lore lore = new Lore();
        lore.add(MM."<dark_gray>Mask Armor Piece");
        lore.addEmpty();
        if (selected) {
            lore.add("<green>This is the armor piece!");
        } else {
            lore.add("<cta>Click to use material!");
        }

        return MenuStack.of(material).name(name).lore(lore).glowing(selected).build();
    }
}
