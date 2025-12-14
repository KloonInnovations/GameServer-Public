package io.kloon.gameserver.creative.menu.create.datacenter;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.MiniMessageTemplate;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.menu.create.CreateWorldMenu;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.facts.KloonDataCenter;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Arrays;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ChooseWorldDatacenterMenu extends ChestMenu {
    private final CreateWorldMenu menu;

    public static final List<KloonDataCenter> PROD_DATACENTERS = Arrays.stream(KloonDataCenter.values())
            .filter(KloonDataCenter::canBePickedByPlayers)
            .toList();

    public ChooseWorldDatacenterMenu(CreateWorldMenu menu) {
        super("Choose Datacenter");
        this.menu = menu;
    }

    @Override
    protected void registerButtons() {
        ChestLayouts.INSIDE.distribute(PROD_DATACENTERS, (slot, datacenter) -> {
            reg(slot, new ChooseDatacenterButton(datacenter, this::onSelect));
        });

        reg().goBack(menu);
    }

    private void onSelect(Player p, KloonDataCenter datacenter) {
        menu.getState().setDatacenter(datacenter);
        menu.display(p);
    }

    @Override
    public ItemStack renderButton(Player p) {
        KloonPlayer player = (KloonPlayer) p;

        Component name = MM."<title>Datacenter Region";

        Lore lore = new Lore();
        lore.wrap("<gray>Where on <#18D5FF>Earth <gray>this world is hosted.");
        lore.addEmpty();

        KloonDataCenter datacenter = menu.getState().getDatacenter(player);
        lore.add(MM."<gray>Region: <\{MiniMessageTemplate.INFRA_COLOR.asHexString()}>\{datacenter.getRegionName()}");

        lore.addEmpty();
        lore.wrap("<gray>Pick a datacenter close to you to reduce ping.");
        lore.addEmpty();
        lore.add("<cta>Click to select!");

        return MenuStack.of(Material.CRAFTER, name, lore);
    }
}
