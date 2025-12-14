package io.kloon.gameserver.games;

import java.util.UUID;

public record CreateCreativeInstanceReply(
        UUID instanceId,
        boolean created
) {
}
