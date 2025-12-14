package io.kloon.gameserver.modes.creative.menu.history.records;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.components.ComponentWrapper;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.OngoingChange;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class OngoingButton implements ChestButton {
    private final OngoingChange ongoing;

    public OngoingButton(OngoingChange ongoing) {
        this.ongoing = ongoing;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player player) {
        ChangeMeta meta = ongoing.meta();

        Component name = MM."\{meta.changeTitleMM()}";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<dark_gray>Change is ongoing");
        lore.add(Component.empty());

        lore.addAll(ComponentWrapper.wrap(meta.chatText(), 28));
        lore.add(Component.empty());

        lore.add(MM."<gray>Tool: <white>\{meta.tool().getDisplayName()}");

        return MenuStack.of(meta.tool().getMaterial()).name(name).lore(lore).build();
    }
}
