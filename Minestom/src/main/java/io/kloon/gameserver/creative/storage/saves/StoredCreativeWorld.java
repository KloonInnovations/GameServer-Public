package io.kloon.gameserver.creative.storage.saves;

public record StoredCreativeWorld(
        byte[] polarBytes,
        byte[] customBytes
) {
}
