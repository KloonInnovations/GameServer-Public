package io.kloon.gameserver.modes.hub.hubslist;

import io.kloon.bigbackend.client.admin.TransferClient;
import io.kloon.bigbackend.games.hub.HubEntry;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.MiniMessageTemplate;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.autoupdate.AutoUpdateButton;
import io.kloon.gameserver.chestmenus.states.ButtonState;
import io.kloon.gameserver.chestmenus.states.ButtonStateTracker;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import io.kloon.infra.facts.KloonDataCenter;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class HubListButton implements ChestButton, AutoUpdateButton {
    private static final Logger LOG = LoggerFactory.getLogger(HubListButton.class);

    private final int slot;
    private final int index;
    private final HubEntry entry;

    private static final PlayerTickCooldownMap GLOBAL_COOLDOWN = new PlayerTickCooldownMap(50);

    private final ButtonStateTracker stateTracker = new ButtonStateTracker(this::getState);

    public HubListButton(int slot, int index, HubEntry entry) {
        this.slot = slot;
        this.index = index;
        this.entry = entry;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        ButtonState state = getState(player);
        if (state != CAN_TRANSFER) {
            player.sendMessage(state.getChatMessage(player));
            return;
        }

        GLOBAL_COOLDOWN.get(player).cooldown();
        ChestMenuInv.rerenderButton(slot, player);

        TransferClient transferClient = Kgs.getBackend().getTransfers();

        KloonPlayer kp = (KloonPlayer) player;
        kp.allocateAndTransfer(p -> transferClient.allocateInstanceTransfer(entry.serverAllocation(), entry.instanceUuid(), p));
    }

    @Override
    public ItemStack renderButton(Player player) {
        List<Component> lore = new ArrayList<>();

        Component name = MM."<title>Hub #\{index + 1}";

        lore.add(MM."<dark_gray>\{entry.cuteName()}");
        lore.add(Component.empty());

        lore.add(MM."<gray>Players: <green>\{entry.players()}/\{entry.maxPlayers()}");

        KloonDataCenter datacenter = KloonDataCenter.parse(entry.datacenterKey());
        String regionColor = MiniMessageTemplate.INFRA_COLOR.asHexString();
        lore.add(MM."<gray>Region: <\{regionColor}>\{datacenter.getRegionName()}");
        lore.add(Component.empty());

        ButtonState state = getState(player);

        lore.add(state.getCallToAction(player));

        return MenuStack.of(state.getIcon(Material.WHITE_CONCRETE))
                .name(name)
                .lore(lore)
                .build();
    }

    @Override
    public boolean shouldRerender(Player player) {
        return stateTracker.checkChanged(player);
    }

    private static final ButtonState CAN_TRANSFER = new ButtonState("<cta>Click to switch!");
    private static final ButtonState YOU_ARE_HERE = new ButtonState("<green>You are here!").withIcon(Material.GREEN_CONCRETE);
    private static final ButtonState TRANSFERRING = new ButtonState("<green>Transferring...").withChat("<green>You are currently transferring to this hub... Hopefully it works!").withIcon(Material.FEATHER);
    private static final ButtonState ON_COOLDOWN = new ButtonState("<!cta>On cooldown!").withChat("<red>Switching hubs is on cooldown!");

    private ButtonState getState(Player player) {
        if (Kgs.getInfra().allocationName().equals(entry.serverAllocation())) {
            return YOU_ARE_HERE;
        }
        if (GLOBAL_COOLDOWN.get(player).isOnCooldown()) {
            return ON_COOLDOWN;
        }
        return CAN_TRANSFER;
    }
}
