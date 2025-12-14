package io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.anchors;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipePlayerStorage;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class AddRangeAnchorButton implements ChestButton {
    private final RangeAnchorsMenu menu;

    public AddRangeAnchorButton(RangeAnchorsMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        double min = 0.0;
        double max = SnipePlayerStorage.MAX_RANGE;

        new SignUXNumberInput().bounds(min, max).display(player, "Range (blocks)", range -> {
            SnipePlayerStorage snipeStorage = player.getCreativeStorage().getSnipe();
            List<Double> anchors = snipeStorage.getRangeAnchors();
            anchors.add(range);
            snipeStorage.setRangeAnchors(anchors);

            player.playSound(SoundEvent.ENTITY_VILLAGER_WORK_MASON, Pitch.rng(1.5, 0.15));
            menu.reload().display(player);
        });
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Add Range Anchor";

        Lore lore = new Lore();
        lore.wrap("<gray>Adds a new anchor to the q-range shortcut.");
        lore.addEmpty();
        lore.add("<cta>Click to add");

        return MenuStack.of(Material.OAK_SIGN, name, lore);
    }
}
