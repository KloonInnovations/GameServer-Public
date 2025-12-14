package io.kloon.gameserver.modes.creative.menu.history.records;

import humanize.Humanize;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.components.ComponentWrapper;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.history.RedoCommand;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeRecord;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class FutureRecordButton implements ChestButton {
    private final ChangeRecord record;
    private final int index;

    public FutureRecordButton(ChangeRecord record, int index) {
        this.record = record;
        this.index = index;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        if (index == 0) {
            RedoCommand.redo(player);
            ChestMenuInv.rerender(player);
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        ChangeMeta meta = record.meta();

        Component name = MM."\{meta.changeTitleMM()}";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<dark_gray>\{Humanize.ordinal(index + 1)} to /redo");
        lore.add(Component.empty());

        lore.addAll(ComponentWrapper.wrap(meta.chatText(), 28));
        lore.add(Component.empty());

        lore.add(MM."<gray>Tool: <white>\{meta.tool().getDisplayName()}");

        if (index == 0) {
            lore.add(Component.empty());
            lore.add(MM."<cta>Click to redo!");
        }

        return MenuStack.of(meta.tool().getMaterial()).name(name).lore(lore).build();
    }
}
