package io.kloon.gameserver.creative.menu.create;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.CreativeWorldsMenu;
import io.kloon.gameserver.creative.menu.ActuallyCreateWorldButton;
import io.kloon.gameserver.creative.menu.commands.CreateWorldCommand;
import io.kloon.gameserver.creative.menu.create.datacenter.ChooseWorldDatacenterMenu;
import io.kloon.gameserver.creative.menu.create.dimensions.ChooseDimensionMenu;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.ranks.StoreRank;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class CreateWorldMenu extends ChestMenu {
    private final ChestMenu parent;

    private final WorldCreationState state;

    public CreateWorldMenu(ChestMenu parent) {
        this(parent, new WorldCreationState());
    }

    public CreateWorldMenu(ChestMenu parent, WorldCreationState creationState) {
        super("New Creative World", ChestSize.FOUR);
        this.parent = parent;
        this.state = creationState;

        setTitleFunction(p -> {
            CopyingWorld copying = creationState.getCopyingWorld();
            return copying == null
                    ? MM."New Creative World"
                    : MM."Copying Creative World";
        });
    }

    public WorldCreationState getState() {
        return state;
    }

    @Override
    protected void registerButtons() {
        reg(10, new EditNameButton(this));
        reg(12, new ChooseWorldDatacenterMenu(this));
        reg(14, new ChooseDimensionMenu(this));
        reg(16, new ActuallyCreateWorldButton(state));

        reg().goBack(parent);
    }
}
