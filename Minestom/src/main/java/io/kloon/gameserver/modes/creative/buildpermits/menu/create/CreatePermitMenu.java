package io.kloon.gameserver.modes.creative.buildpermits.menu.create;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.storage.defs.BuildPermit;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.buildpermits.menu.BuildPermitsMenu;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.duration.PermitDurationMenu;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.recipient.InputRecipientButton;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.recipient.PermitRecipientSelectionMenu;
import io.kloon.gameserver.modes.creative.commands.BuildPermitsCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreatePermitMenu extends ChestMenu {
    private final BuildPermitsMenu parent;

    private final CreatePermitState state = new CreatePermitState();

    public CreatePermitMenu(BuildPermitsMenu parent) {
        super("Create Build Permit", ChestSize.FOUR);
        this.parent = parent;
    }

    public CreativePlayer getPlayer() {
        return parent.getPlayer();
    }

    public CreatePermitState getState() {
        return state;
    }

    @Override
    protected void registerButtons() {
        reg(11, new PermitRecipientSelectionMenu(this));
        reg(13, new PermitDurationMenu(this));
        reg(15, new IssuePermitButton(parent, this));

        reg().goBack(parent);
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        if (getEligiblePlayers().isEmpty()) {
            return;
        }

        super.clickButton(p, click);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;
        Component name = MM."<title>Issue New Permit";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{BuildPermitsCommand.LABEL_SHORT} <username>");
        lore.addEmpty();

        lore.wrap("<gray>Create a new permit, allowing another player to build on this world.");
        lore.addEmpty();

        List<CreativePlayer> eligiblePlayers = getEligiblePlayers();
        if (eligiblePlayers.isEmpty()) {
            lore.add("<!cta>No eligible player in world!");
        } else {
            lore.add("<cta>Click for setup!");
        }

        return MenuStack.of(Material.LECTERN, name, lore);
    }

    public List<CreativePlayer> getEligiblePlayers() {
        CreativePlayer viewer = parent.getPlayer();
        CreativeInstance instance = viewer.getInstance();
        return instance.streamPlayers()
                .filter(p -> {
                    if (p == viewer) return false;
                    BuildPermit permit = instance.getPermitForPlayer(p);
                    if (permit != null) return false;
                    return true;
                })
                .toList();
    }
}
