package io.kloon.gameserver.modes.creative.buildpermits;

import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.blockedits.authorization.BlockEditAuthorizationEvent;
import io.kloon.gameserver.modes.creative.blockedits.authorization.BlockEditDenial;

public class BuildPermitListener {
    @EventHandler
    public void onEdit(BlockEditAuthorizationEvent event) {
        CreativePlayer player = event.getPlayer();
        if (!player.canEditWorld()) {
            event.deny(BlockEditDenial.Source.NO_PERMIT, player::sendCantEditWorldMessage);
        }
    }
}
