package io.kloon.gameserver.modes.creative.buildpermits.menu.create.recipient;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.autoupdate.AutoUpdateMenu;
import io.kloon.gameserver.chestmenus.builtin.StaticButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.pagination.MenuPagination;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitMenu;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitState;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.timer.TaskSchedule;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class PermitRecipientSelectionMenu extends ChestMenu implements AutoUpdateMenu {
    private final CreatePermitMenu createPermitMenu;

    private final CreativePlayer player;
    private final CreativeInstance instance;

    private final CreatePermitState state;

    private final MenuPagination pagination;

    private int renderedPlayers = 0;

    public PermitRecipientSelectionMenu(CreatePermitMenu createPermitMenu) {
        super("Permit Recipient Selection");
        this.createPermitMenu = createPermitMenu;
        this.state = createPermitMenu.getState();
        this.player = createPermitMenu.getPlayer();
        this.instance = player.getInstance();
        this.pagination = new MenuPagination(this, ChestLayouts.INSIDE);
    }

    @Override
    protected void registerButtons() {
        List<SelectRecipientButton> buttons = createPermitMenu.getEligiblePlayers().stream()
                .map(p -> new SelectRecipientButton(createPermitMenu, this, p.getUuid()))
                .toList();
        if (buttons.isEmpty()) {
            reg(size.middleCenter(), MenuStack.of(Material.ORANGE_STAINED_GLASS)
                    .name(MM."<red>Where is everyone?").lore(MM_WRAP."<gray>There's no one in this list anymore. They probably logged off.")
                    .buildButton());
        } else {
            pagination.distribute(buttons, this::reg);
        }
        this.renderedPlayers = buttons.size();

        reg().goBack(createPermitMenu);
        reg(size.bottomCenter() + 1, new InputRecipientButton(createPermitMenu));
    }

    public CreativeInstance getInstance() {
        return instance;
    }

    @Override
    public ItemStack renderButton(Player player) {
        KloonPlayer recipient = state.getRecipient(player.getInstance());
        String titleUsernameMM = recipient == null ? null : recipient.getDisplayMM();

        Component name = titleUsernameMM == null
                ? MM."<title>Select Recipient"
                : MM."<title>Recipient: \{titleUsernameMM}";

        Lore lore = new Lore();
        lore.wrap("<gray>Pick who you want to receive the build permit.");
        lore.addEmpty();
        lore.wrap("<yellow>⚠ Note! <gray>The player needs to be in this instance!");

        if (state.getRecipientUuid() != null) {
            lore.addEmpty();
            String recipientName = recipient == null
                    ? "<red>Unknown!"
                    : recipient.getUsername();
            lore.add(MM."<gray>Recipient: <white>\{recipientName}");
        }

        lore.addEmpty();
        lore.add("<cta>Click to pick!");

        if (recipient == null) {
            return MenuStack.of(Material.TOTEM_OF_UNDYING, name, lore);
        } else {
            HeadProfile skin = SkinCache.get(recipient);
            return MenuStack.ofHead(skin).name(name).lore(lore).build();
        }
    }

    @Override
    public TaskSchedule getAutoUpdatePeriod() {
        return TaskSchedule.tick(2 * 20 + 10);
    }

    @Override
    public boolean shouldReloadMenu() {
        return createPermitMenu.getEligiblePlayers().size() != renderedPlayers;
    }
}
