package io.kloon.gameserver.modes.creative.buildpermits.menu.create.recipient;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitMenu;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitState;
import io.kloon.gameserver.modes.creative.commands.BuildPermitsCommand;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class InputRecipientButton implements ChestButton {
    private final CreatePermitMenu parent;
    private final CreatePermitState state;

    public InputRecipientButton(CreatePermitMenu parent) {
        this.parent = parent;
        this.state = parent.getState();
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        KloonPlayer player = (KloonPlayer) p;

        String[] inputLines = SignUX.inputLines("Enter username", "for permit");
        SignUX.display(player, inputLines, inputs -> {
            String input = inputs[0];
            editState(player, input, state);
            parent.reload().display(player);
        });
    }

    public static boolean editState(KloonPlayer editor, String input, CreatePermitState state) {
        Player playerFromInput = getPlayerFromInput(editor.getInstance(), input);
        if (playerFromInput == null) {
            editor.playSound(SoundEvent.ENTITY_VILLAGER_NO, 1f);
            editor.sendPit(NamedTextColor.RED, "NOT FOUND!", MM."<gray>Couldn't find player with name <white>\"\{input}\"<gray>!");
            editor.sendPit(NamedTextColor.YELLOW, "NOTE!", MM."<gray>The permit recipient needs to be on this instance!");
            return false;
        }
        state.setRecipientUuid(playerFromInput.getUuid());
        return true;
    }

    @Nullable
    private static Player getPlayerFromInput(Instance instance, String input) {
        return instance.getPlayers().stream()
                .filter(player -> player.getUsername().equalsIgnoreCase(input))
                .findFirst().orElse(null);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Player recipient = state.getRecipient(player.getInstance());
        Component name = MM."<title>Enter Username";

        Lore lore = new Lore();
        lore.wrap("<gray>Enter the name of the permit recipient, maybe it's faster...");

        lore.addEmpty();
        lore.wrap("<yellow>⚠ Note! <gray>The player needs to be in this instance!");

        lore.addEmpty();
        lore.add(MM."<gray>Shortcut: <green>/\{BuildPermitsCommand.LABEL_SHORT} <username>");
        lore.add("<dark_gray>\uD83D\uDDAE It's a command!"); // 🖮

        if (state.getRecipientUuid() != null) {
            lore.addEmpty();
            String recipientName = recipient == null
                    ? "<red>Unknown!"
                    : recipient.getUsername();
            lore.add(MM."<gray>Recipient: <white>\{recipientName}");
        }

        lore.addEmpty();
        lore.add("<cta>Click to enter name!");

        return MenuStack.of(Material.OAK_SIGN, name, lore);
    }
}
