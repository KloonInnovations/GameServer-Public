package io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.anchors;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipePlayerStorage;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.Arrays;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class RangeAnchorButton implements ChestButton {
    private final ChestMenu menu;
    private final int index;
    private final double range;

    public RangeAnchorButton(ChestMenu menu, int index, double range) {
        this.menu = menu;
        this.index = index;
        this.range = range;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        if (click.isRightClick()) {
            handleRemove(player);
        } else {
            handleEdit(player);
        }
    }

    private void handleRemove(CreativePlayer player) {
        SnipePlayerStorage snipeStorage = player.getCreativeStorage().getSnipe();
        List<Double> anchors = snipeStorage.getRangeAnchors();
        if (anchors.size() <= 1) {
            player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 0.9);
            return;
        }

        player.playSound(SoundEvent.ENTITY_VILLAGER_WORK_MASON, 0.5);
        anchors.remove(range);
        snipeStorage.setRangeAnchors(anchors);
        menu.reload().display(player);
    }

    private void handleEdit(CreativePlayer player) {
        double min = 0.0;
        double max = SnipePlayerStorage.MAX_RANGE;

        new SignUXNumberInput().bounds(min, max).display(player, "Range (blocks)", updatedRange -> {
            SnipePlayerStorage snipeStorage = player.getCreativeStorage().getSnipe();
            List<Double> anchors = snipeStorage.getRangeAnchors();
            anchors.remove(this.range);
            anchors.add(updatedRange);
            snipeStorage.setRangeAnchors(anchors);

            player.playSound(SoundEvent.ENTITY_VILLAGER_WORK_MASON, 1.3);
            menu.reload().display(player);
        });
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        SnipePlayerStorage snipeStorage = player.getCreativeStorage().getSnipe();
        List<Double> anchors = snipeStorage.getRangeAnchors();

        Component name = MM."<title>\{NumberFmt.NO_DECIMAL.format(range)} blocks";

        Lore lore = new Lore();
        if (anchors.size() <= 1) {
            lore.wrap("<gray>If Q-Range is enabled, the drop key will snap your tool's range to this range.");
            lore.addEmpty();
            lore.add("<lcta>Click to edit!");
        } else {
            lore.wrap("<gray>If Q-Range is enabled, the drop key will snap your tool's range to the nearest or next range anchor.");
            lore.addEmpty();
            lore.add("<rcta>Click to remove!");
            lore.add("<lcta>Click to edit!");
        }

        Material icon = ICONS.get(index);

        return MenuStack.of(icon, name, lore);
    }

    public static final List<Material> ICONS = Arrays.asList(
            Material.BLUE_TERRACOTTA,
            Material.LIGHT_BLUE_TERRACOTTA,
            Material.CYAN_TERRACOTTA,
            Material.GREEN_TERRACOTTA,
            Material.LIME_TERRACOTTA,
            Material.YELLOW_TERRACOTTA,
            Material.ORANGE_TERRACOTTA,
            Material.RED_TERRACOTTA
    );
}
