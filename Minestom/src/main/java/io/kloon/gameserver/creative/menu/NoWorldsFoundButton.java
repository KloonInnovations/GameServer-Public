package io.kloon.gameserver.creative.menu;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.CreativeWorldsMenuProxy;
import io.kloon.gameserver.creative.menu.commands.CreateWorldCommand;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.owner.player.PlayerWorldOwner;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.RandUtil;
import io.kloon.infra.facts.KloonDataCenter;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.bson.types.ObjectId;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class NoWorldsFoundButton implements ChestButton {
    @Override
    public void clickButton(Player player, ButtonClick click) {
        KloonDataCenter datacenter = Kgs.getInfra().datacenter();

        ObjectId worldId = new ObjectId();
        String worldName = STR."\{player.getUsername()}'s World";

        KloonPlayer kp = (KloonPlayer) player;
        WorldOwner owner = new PlayerWorldOwner(kp);
        Material icon = RandUtil.getRandom(CreativeWorldsMenuProxy.GLAZED);
        WorldDef worldDef = new WorldDef(worldId, worldName, icon, owner, datacenter);

        player.sendMessage(MM."<gray>Creating your first creative world...");
        CreateWorldCommand.createWorld(kp, worldDef);
    }

    @Override
    public ItemStack renderButton(Player player) {
        List<Component> lore = MM_WRAP."<gray>Hey, you do not have any creative worlds on the server!";
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to create world!");

        return MenuStack.of(Material.GREEN_GLAZED_TERRACOTTA).name(MM."<title>Create First World").lore(lore).build();
    }
}
