package io.kloon.gameserver.creative.menu.manage;

import com.google.common.base.Strings;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.menu.create.EditNameButton;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.defs.WorldDefRepo;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class EditNameManageButton implements ChestButton {
    private static final Logger LOG = LoggerFactory.getLogger(EditNameManageButton.class);

    private final ChestMenu parent;
    private final WorldDef worldDef;

    public EditNameManageButton(ChestMenu parent, WorldDef worldDef) {
        this.parent = parent;
        this.worldDef = worldDef;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        KloonPlayer player = (KloonPlayer) p;

        String oldName = worldDef.name();

        String[] inputLines = SignUX.inputLines("Enter revised", "world name");
        SignUX.display(player, EditNameButton.SIGN.block(), inputLines, input -> {
            String newName = input[0];
            if (Strings.isNullOrEmpty(newName)) {
                player.sendPit(NamedTextColor.RED, "WHAT?", MM."<gray>You entered an empty name!");
                parent.display(player);
                return;
            }

            WorldDefRepo repo = Kgs.getCreativeRepos().defs();
            player.closeInventory();
            if (newName.equals(oldName)) {
                player.sendPit(NamedTextColor.RED, "GOOD NEWS!", MM."<gray>This is already the world's name!");
                return;
            }
            worldDef.setName(newName);
            repo.update(worldDef).whenCompleteAsync((_, t) -> {
                if (t != null) {
                    LOG.error("Error setting world name", t);
                    player.sendPitError(MM."<red>Problem editing the world's name!");
                }
                worldDef.broadcastInvalidate();
                player.sendPit(NamedTextColor.GREEN, "RENAMED!", MM."<gray>World from <red>\{oldName } <gray>to <green>\{newName}<gray>, much cooler!");
            }, player.scheduler());
        });
    }

    @Override
    public ItemStack renderButton(Player player) {
        String currentName = worldDef.name();

        Component name = MM."<title>Edit World Name";

        List<Component> lore = MM_WRAP."<gray>Edit the name of this world.";
        lore.add(Component.empty());
        lore.add(MM."<gray>Name: <green>\{currentName}");
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to edit!");

        return MenuStack.of(EditNameButton.SIGN).name(name).lore(lore).build();
    }
}
