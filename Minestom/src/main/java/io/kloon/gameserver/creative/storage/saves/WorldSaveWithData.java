package io.kloon.gameserver.creative.storage.saves;

import org.jetbrains.annotations.Nullable;

public record WorldSaveWithData(WorldSave worldSave, byte[] polarBytes, @Nullable byte[] customBytes) {
}