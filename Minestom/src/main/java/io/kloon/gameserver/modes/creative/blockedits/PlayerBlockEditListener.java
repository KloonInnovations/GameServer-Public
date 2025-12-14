package io.kloon.gameserver.modes.creative.blockedits;

import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.modes.creative.blockedits.authorization.BlockEditAuthorizationEvent;
import io.kloon.gameserver.modes.creative.blockedits.authorization.BlockEditDenial;
import io.kloon.gameserver.modes.creative.blockedits.byhand.CreativeBlockBrokenByHandEvent;
import io.kloon.gameserver.modes.creative.blockedits.byhand.CreativeBlockPlacedByHandEvent;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PlayerBlockEditListener {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerBlockEditListener.class);

    @EventHandler
    public void onBreak(PlayerBlockBreakEvent breakEvent) {
        BlockEditAuthorizationEvent authorizeEvent = new BlockEditAuthorizationEvent(breakEvent);
        EventDispatcher.call(authorizeEvent);

        if (isDenied(authorizeEvent)) {
            breakEvent.setCancelled(true);
        } else {
            EventDispatcher.call(new CreativeBlockBrokenByHandEvent(breakEvent));
        }
    }

    @EventHandler
    public void onPlace(PlayerBlockPlaceEvent placeEvent) {
        BlockEditAuthorizationEvent authorizeEvent = new BlockEditAuthorizationEvent(placeEvent);
        EventDispatcher.call(authorizeEvent);

        if (isDenied(authorizeEvent)) {
            placeEvent.setCancelled(true);
        } else {
            // sent in CreativeInstance
            //EventDispatcher.call(new CreativeBlockPlacedByHandEvent(placeEvent));
        }
    }

    // returns true if should cancel
    private boolean isDenied(BlockEditAuthorizationEvent event) {
        List<BlockEditDenial> authorizations = new ArrayList<>(event.getDenials());
        if (authorizations.isEmpty()) {
            return false;
        }

        List<BlockEditDenial> objections = authorizations.stream()
                .sorted(Comparator.comparingInt(e -> e.source().ordinal()))
                .toList();
        if (objections.isEmpty()) {
            return false;
        }

        BlockEditDenial blocker = authorizations.getLast();
        try {
            blocker.callbackIfBlocker().run();
        } catch (Throwable t) {
            LOG.error("Error in block authorization callback", t);
            event.getPlayer().sendPitError(MM."<gray>Error telling you why you can't do this!");
        }
        return true;
    }
}
