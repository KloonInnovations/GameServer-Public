package io.kloon.gameserver.games;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record CreateCreativeInstanceRequest(
        UUID requestId,
        String worldIdHex,
        @Nullable String saveIdHex
) {
}
