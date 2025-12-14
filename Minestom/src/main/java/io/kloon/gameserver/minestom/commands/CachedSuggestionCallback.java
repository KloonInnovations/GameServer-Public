package io.kloon.gameserver.minestom.commands;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionCallback;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class CachedSuggestionCallback implements SuggestionCallback {
    private final Cache<UUID, List<SuggestionEntry>> cache;

    public CachedSuggestionCallback() {
        this(3, TimeUnit.SECONDS);
    }

    public CachedSuggestionCallback(long duration, TimeUnit timeUnit) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(duration, timeUnit)
                .build();
    }

    @Override
    public final void apply(@NotNull CommandSender sender, @NotNull CommandContext context, @NotNull Suggestion suggestion) {
        if (!(sender instanceof KloonPlayer player)) return;
        List<SuggestionEntry> entries = cache.get(player.getUuid(), _ -> compute(player, context));
        entries.forEach(suggestion::addEntry);
    }

    public abstract List<SuggestionEntry> compute(@NotNull KloonPlayer player, @NotNull CommandContext context);
}
