package io.kloon.gameserver.creative.menu.manage.oldsaves;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.StaticButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import net.minestom.server.item.Material;

import java.util.Comparator;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class WorldSavesMenu extends ChestMenu {
    private final ChestMenu parent;
    private final WorldDef world;
    private final List<WorldSave> saves;

    public WorldSavesMenu(ChestMenu parent, WorldDef world, List<WorldSave> saves) {
        super("World Saves");
        this.parent = parent;
        this.world = world;
        this.saves = saves;

        setTitleFunction(p -> MM."Saves for world: \{world.name()}");
    }

    @Override
    protected void registerButtons() {
        List<WorldSave> sorted = saves.stream()
                .sorted(Comparator.comparingLong(s -> -s.timestamp()))
                .toList();

        if (saves.isEmpty()) {
            reg(size.middleCenter(), new StaticButton(MenuStack.of(Material.BARRIER)
                    .name(MM."<red>No saves!")
                    .lore(MM_WRAP."<gray>There are no saves for this world.")));
        } else {
            ChestLayouts.INSIDE.distribute(sorted, (slot, save) -> {
                int index = sorted.indexOf(save);
                boolean isLatest = index == 0;
                WorldSave prev = index >= sorted.size() - 1 ? null : sorted.get(index + 1);
                OldWorldSave oldSave = new OldWorldSave(world, save, prev, isLatest);
                reg(slot, new OldSaveButton(this, oldSave));
            });
        }

        reg().goBack(parent);
    }
}
