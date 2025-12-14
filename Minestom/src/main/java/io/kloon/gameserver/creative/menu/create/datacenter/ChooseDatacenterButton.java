package io.kloon.gameserver.creative.menu.create.datacenter;

import io.kloon.gameserver.MiniMessageTemplate;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.facts.KloonDataCenter;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.function.BiConsumer;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ChooseDatacenterButton implements ChestButton {
    private final KloonDataCenter datacenter;
    private final BiConsumer<Player, KloonDataCenter> onSelect;

    public ChooseDatacenterButton(KloonDataCenter datacenter, BiConsumer<Player, KloonDataCenter> onSelect) {
        this.datacenter = datacenter;
        this.onSelect = onSelect;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        onSelect.accept(player, datacenter);
    }

    @Override
    public ItemStack renderButton(Player p) {
        KloonPlayer player = (KloonPlayer) p;

        Component name = MM."<title>\{datacenter.getRegionName()}";

        Lore lore = new Lore();
        lore.wrap("<gray>An incredibly charming residence for your world.");
        lore.addEmpty();

        KloonDataCenter connectedDc = player.getProxyInfo().datacenter();
        if (datacenter == connectedDc) {
            lore.wrap(MM."<\{MiniMessageTemplate.INFRA_COLOR.asHexString()}>You are connected through this region!");
            lore.addEmpty();
        }

        lore.add("<cta>Click to select!");

        ItemBuilder2 icon = MenuStack.ofHead(datacenter.getSkinValue());
        return icon.name(name).lore(lore).build();
    }
}
