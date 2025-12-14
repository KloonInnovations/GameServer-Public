package io.kloon.gameserver.modes.creative.buildpermits.menu.create.recipient;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.states.ButtonState;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.storage.defs.BuildPermit;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitMenu;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitState;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.RandUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SelectRecipientButton implements ChestButton {
    private final CreatePermitMenu createPermitMenu;
    private final ChestMenu parent;

    private final CreatePermitState permitState;

    private final UUID targetId;

    public SelectRecipientButton(CreatePermitMenu createPermitMenu, ChestMenu parent, UUID targetId) {
        this.createPermitMenu = createPermitMenu;
        this.permitState = createPermitMenu.getState();
        this.parent = parent;
        this.targetId = targetId;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        ButtonState state = getState(player);
        if (state != CAN_SELECT) {
            state.sendChatMessage(player);
            parent.reload().display(player);
            return;
        }

        permitState.setRecipientUuid(targetId);
        createPermitMenu.display(player);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        CreativePlayer target = player.getInstance().getPlayerByUuid(targetId);
        if (target == null) {
            return MenuStack.of(Material.CREEPER_HEAD, MM."<red>Player offline!", new Lore().wrap("<gray>Whoever that was, they're offline now!"));
        }

        Component name = MM."\{target.getDisplayMM()}";

        Lore lore = new Lore();
        String niceLine = getNiceLine(targetId);
        lore.add(MM."<gray>\{niceLine}");
        lore.addEmpty();

        ButtonState state = getState(player);
        lore.add(state.getCallToAction(player));

        HeadProfile head = SkinCache.get(target);
        return MenuStack.of(Material.PLAYER_HEAD)
                .set(DataComponents.PROFILE, head)
                .name(name)
                .lore(lore)
                .build();
    }

    private ButtonState getState(CreativePlayer player) {
        CreativeInstance instance = player.getInstance();
        CreativePlayer target = instance.getPlayerByUuid(targetId);
        if (target == null) {
            return TARGET_OFFLINE;
        }

        if (target == player) {
            return IS_PLAYER;
        }

        BuildPermit existingPermit = instance.getPermitForPlayer(target);
        if (existingPermit != null) {
            return ALREADY_HAS_PERMIT;
        }

        return CAN_SELECT;
    }

    private static final ButtonState CAN_SELECT = new ButtonState("<cta>Click to select!");
    private static final ButtonState IS_PLAYER = new ButtonState("<gold>✨ <#FF266E>That's you!!");
    private static final ButtonState ALREADY_HAS_PERMIT = new ButtonState("<gold>Already has a permit!");
    private static final ButtonState TARGET_OFFLINE = new ButtonState("<!cta>Player is offline!");

    private String getNiceLine(UUID uuid) {
        int someHash = MinecraftServer.getServer().hashCode();
        int anotherHash = Objects.hash(someHash, uuid);
        return RandUtil.getRandom(NICE_LINES, anotherHash);
    }

    private static final List<String> NICE_LINES = Arrays.asList(
            "They seem nice!",
            "They seem cool!",
            "They seem okay!",
            "They seem fun!",
            "They seem kind!",
            "They seem great!",
            "They seem considerate!",
            "They seem fiendly!",
            "They seem warm!",
            "They seem like they'd share their Wi-Fi password!"
    );
}
