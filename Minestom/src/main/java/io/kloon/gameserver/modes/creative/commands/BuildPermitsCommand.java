package io.kloon.gameserver.modes.creative.commands;

import io.kloon.gameserver.minestom.commands.CachedSuggestionCallback;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.buildpermits.menu.BuildPermitsMenu;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitMenu;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.recipient.InputRecipientButton;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class BuildPermitsCommand extends Command {
    public static final String LABEL = "permits";
    public static final String LABEL_SHORT = "permit";

    public BuildPermitsCommand() {
        super(LABEL, LABEL_SHORT);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                new BuildPermitsMenu(player.createMainMenu(), player).display(player);
            }
        });

        ArgumentString usernameArg = ArgumentType.String("username");
        usernameArg.setSuggestionCallback(new CachedSuggestionCallback(3, TimeUnit.SECONDS) {
            public List<SuggestionEntry> compute(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                return player.getInstance().getPlayers().stream()
                        .map(Player::getUsername)
                        .map(SuggestionEntry::new)
                        .toList();
            }
        });
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                if (!BuildPermitsMenu.canManagePermits(player)) {
                    player.sendPit(NamedTextColor.RED, "NOPE!", MM."<gray>You may not create build permits on this world!");
                    return;
                }

                BuildPermitsMenu buildPermitsMenu = new BuildPermitsMenu(player.createMainMenu(), player);
                CreatePermitMenu createPermitMenu = new CreatePermitMenu(buildPermitsMenu);

                String input = context.get(usernameArg);
                boolean valid = InputRecipientButton.editState(player, input, createPermitMenu.getState());
                if (valid) {
                    createPermitMenu.display(player);
                }
            }
        }, usernameArg);
    }
}
