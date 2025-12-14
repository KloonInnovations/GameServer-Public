package io.kloon.gameserver.modes.creative.menu.patterns.use;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayout;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.patterns.PatternSelectionProxy;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.PatternType;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ChoosePatternMenu extends ChestMenu {
    private final ChestMenu parent;
    private final CreativeConsumer<CreativePattern> callback;

    private @Nullable CreativePattern existing;

    public ChoosePatternMenu(ChestMenu parent, CreativeConsumer<CreativePattern> callback) {
        super(parent instanceof CreativeMainMenu ? STR."\{PatternSelectionProxy.ICON} Block Patterns" : "Choose Pattern Type");
        this.parent = parent;
        this.callback = callback;

        if (!(parent instanceof CreativeMainMenu)) {
            setBreadcrumbs(parent, "Choose Pattern Type", "Picking what kind of pattern...");
        }
    }

    public ChoosePatternMenu editing(@Nullable CreativePattern pattern) {
        this.existing = pattern;
        return this;
    }

    @Override
    protected void registerButtons() {
        List<PatternType> types = PatternType.TYPES_EXCEPT_SINGLE;
        ChestLayout layout = ChestLayouts.centeredSecondRowSpaced(types.size());
        layout.distribute(types, (slot, patternType) ->{
            reg(slot + 9, new CreatePatternTypeButton(patternType, callback));
        });

        reg().goBack(parent);
        reg().breadcrumbs();
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        if (click.isRightClick()) {
            if (existing == null || existing instanceof SingleBlockPattern) {
                super.clickButton(player, click);
                return;
            }

            ChestMenu editMenu = existing.getType().createEditMenu(parent, existing, callback);
            if (editMenu != null) {
                editMenu.clickButton(player, click);
                return;
            }
        }

        super.clickButton(player, click);
    }

    @Override
    public ItemStack renderButton(Player player) {
        if (existing == null || existing instanceof SingleBlockPattern) {
            Component name = MM."<title>Use Block Pattern";

            Lore lore = new Lore();
            lore.wrap("<gray>Rather than a single block, use a <i>pattern</i> which decides what block to use.");
            lore.addEmpty();
            lore.add("<cta>Click to use a pattern!");

            return MenuStack.of(Material.PINK_GLAZED_TERRACOTTA, name, lore);
        } else {
            Component name = MM."<title>Edit Block Pattern";

            Lore lore = new Lore();
            lore.add(MM."<gray>Type: <light_purple>\{existing.getType().getName()}");
            lore.add(existing.lore());
            lore.addEmpty();
            if (existing.getType().hasEditMenu()) {
                lore.add("<rcta>Click to edit this pattern!");
                lore.add("<lcta>Click to select another pattern!");
            } else {
                lore.add("<cta>Click to select another pattern!");
            }

            return existing.icon().name(name).lore(lore).build();
        }
    }
}
