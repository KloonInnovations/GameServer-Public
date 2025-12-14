package io.kloon.gameserver.modes.creative.menu.patterns.use;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.PatternType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreatePatternTypeButton implements ChestButton {
    private final PatternType patternType;
    private final CreativeConsumer<CreativePattern> onCreate;

    public CreatePatternTypeButton(PatternType patternType, CreativeConsumer<CreativePattern> onCreate) {
        this.patternType = patternType;
        this.onCreate = onCreate;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        CreativePattern pattern = patternType.createDefaultPattern();
        onCreate.accept(player, pattern);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>\{patternType.getName()}";

        Lore lore = new Lore();
        lore.add("<dark_gray>Pattern Type");
        lore.addEmpty();
        lore.wrap(MM."<gray>\{patternType.getDescription()}");
        lore.addEmpty();
        lore.add("<cta>Click to use this pattern!");

        return patternType.icon().name(name).lore(lore).build();
    }
}
