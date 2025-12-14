package io.kloon.gameserver.minestom.commands;

import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerListSuggestionCallback extends CachedSuggestionCallback {
    @Override
    public List<SuggestionEntry> compute(@NotNull KloonPlayer player, @NotNull CommandContext context) {
        return player.getInstance().getPlayers().stream()
                .map(Player::getUsername)
                .map(SuggestionEntry::new)
                .toList();
    }
}
