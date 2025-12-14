package io.kloon.gameserver.creative.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.states.ButtonState;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.creative.menu.commands.CreateWorldCommand;
import io.kloon.gameserver.creative.menu.create.CopyingWorld;
import io.kloon.gameserver.creative.menu.create.WorldCreationState;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import io.kloon.gameserver.util.formatting.TimeFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class ActuallyCreateWorldButton implements ChestButton {
    private final WorldCreationState creationState;

    private final PlayerTickCooldownMap clickCd = new PlayerTickCooldownMap(45);

    public ActuallyCreateWorldButton(WorldCreationState creationState) {
        this.creationState = creationState;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        KloonPlayer player = (KloonPlayer) p;
        ButtonState state = getState(player);
        if (state != CAN_CREATE) {
            state.sendChatMessage(player);
            return;
        }

        if (!clickCd.get(player).cooldownIfPossible()) {
            player.sendMessage(MM."<red>This functionality is on cooldown!");
            return;
        }

        WorldDef worldDef = creationState.createWorldDef(player);

        player.closeInventory();

        CopyingWorld copying = creationState.getCopyingWorld();
        if (copying == null) {
            CreateWorldCommand.createWorld(player, worldDef);
        } else {
            CreateWorldCommand.createWorldWithCopy(player, worldDef, copying.saveWithData());
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        CopyingWorld copying = creationState.getCopyingWorld();

        Component name = copying == null
                ? MM."<title>Confirm & Create!"
                : MM."<title>Confirm & Copy!";

        List<Component> lore = new ArrayList<>();
        if (copying == null) {
            lore.addAll(MM_WRAP."<gray>Make sure all the settings are to your likings first, then let's go!");
            lore.add(Component.empty());
            lore.addAll(MM_WRAP."<dark_gray>You will be sent to the new world.");
        } else {
            lore.addAll(MM_WRAP."<gray>You are copying a world from a save.");
            lore.add(Component.empty());
            lore.add(MM."<gray>World: <title>\{copying.worldDef().name()}");
            lore.add(MM."<gray>Save: <green>\{copying.save().cuteName()}");
            lore.add(MM."<gray>From: <#FF266E>\{TimeFmt.naturalTime(copying.save().timestamp())}");
            lore.add(Component.empty());
            lore.addAll(MM_WRAP."<dark_gray>You will be sent to the world copy.");
        }
        lore.add(Component.empty());

        ButtonState state = getState(player);
        lore.add(state.getCallToAction(player));

        return MenuStack.of(Material.SLIME_BALL).name(name).lore(lore).build();
    }

    public ButtonState getState(Player player) {
        if (creationState.getName() == null) {
            return MISSING_NAME;
        }
        return CAN_CREATE;
    }

    private static final ButtonState CAN_CREATE = new ButtonState("<cta>Click to create!");
    private static final ButtonState MISSING_NAME = new ButtonState("<!cta>Missing a name!").withChat("<red>The world needs a name before existing!");
}
