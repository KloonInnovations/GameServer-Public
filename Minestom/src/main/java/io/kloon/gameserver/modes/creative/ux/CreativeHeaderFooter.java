package io.kloon.gameserver.modes.creative.ux;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.creative.storage.defs.BuildPermit;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.buildpermits.menu.BuildPermitsMenu;
import io.kloon.gameserver.util.formatting.TimeFmt;
import io.kloon.gameserver.ux.headerfooter.KloonHeaderFooter;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreativeHeaderFooter extends KloonHeaderFooter {
    private final CreativePlayer player;

    public CreativeHeaderFooter(CreativePlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    public List<Component> renderHeader() {
        return Collections.singletonList(MM."<gray>This is a header");
    }

    @Override
    public List<Component> renderFooter() {
        CreativeInstance instance = player.getInstance();
        WorldDef worldDef = instance.getWorldDef();

        Lore lore = new Lore();
        addPingLore(lore);
        lore.addEmpty();

        lore.add(MM."<#FF266E>World");
        lore.add(MM."<white>\{worldDef.name()}");
        lore.add(Component.empty());

        if (instance.getWorldDef().hasEverIssuedPermit()) {
            lore.add(MM."<#FF266E>Owner");
            WorldOwner.Loaded owner = instance.getOwner();
            lore.add(MM."\{owner.getPlayerListLabelMM()}");
            lore.add(Component.empty());
        }

        WorldSave save = instance.getChunkLoader().getLatestSave();
        lore.add(MM."<#FF266E>Last Save");
        if (save == null) {
            lore.add(MM."<red>Brand new world! Get it while it's hot!");
        } else {
            lore.add(MM."<gold>\{save.cuteName()}");

            String dateFmt = TimeFmt.date(save.timestamp());
            //String agoFmt = TimeFmt.naturalTime(save.timestamp());
            lore.add(MM."<gray>taken <aqua>\{dateFmt}");
        }

        return lore.asList();
    }
}
