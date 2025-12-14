package io.kloon.gameserver.modes.creative.tools.impl.waypoint.changes;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.ExceptionResult;
import io.kloon.gameserver.modes.creative.history.results.InstantResult;
import io.kloon.gameserver.modes.creative.storage.datainworld.util.MsgPacked;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointsStorage;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.WaypointEntity;
import net.minestom.server.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RemoveWaypointChange implements Change {
    private static final Logger LOG = LoggerFactory.getLogger(RemoveWaypointChange.class);

    private final MsgPacked<WaypointStorage> serializedWaypoint;

    public RemoveWaypointChange(WaypointStorage waypoint) {
        this.serializedWaypoint = new MsgPacked<>(waypoint, WaypointStorage.class);
    }

    public RemoveWaypointChange(MsgPacked<WaypointStorage> waypoint) {
        this.serializedWaypoint = waypoint;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.REMOVE_WAYPOINT;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        CreativeInstance instance = ctx.instance();

        WaypointsStorage waypoints = instance.getWorldStorage().getWaypoints();

        WaypointStorage waypoint;
        try {
            waypoint = serializedWaypoint.read();
        } catch (Throwable t) {
            LOG.error("Error reading waypoint for undo", t);
            return new ExceptionResult(t);
        }

        waypoints.removeByUuid(waypoint.getUuid());
        Entity entity = instance.getEntityByUuid(waypoint.getUuid());
        if (entity != null) entity.remove();

        waypoints.add(waypoint);
        WaypointEntity.spawn(instance, waypoint);

        return new InstantResult();
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        CreativeInstance instance = ctx.instance();

        WaypointsStorage waypoints = instance.getWorldStorage().getWaypoints();
        WaypointStorage waypoint;
        try {
            waypoint = serializedWaypoint.read();
        } catch (Throwable t) {
            LOG.error("Error reading waypoint for undo", t);
            return new ExceptionResult(t);
        }

        waypoints.removeByUuid(waypoint.getUuid());
        Entity entity = instance.getEntityByUuid(waypoint.getUuid());
        if (entity != null) entity.remove();

        return new InstantResult();
    }

    public static final Codec CODEC = new Codec();
    public static final class Codec implements MinecraftCodec<RemoveWaypointChange> {
        @Override
        public void encode(RemoveWaypointChange obj, MinecraftOutputStream out) throws IOException {
            out.writeByteArray(obj.serializedWaypoint.getSerialized());
        }

        @Override
        public RemoveWaypointChange decode(MinecraftInputStream in) throws IOException {
            MsgPacked<WaypointStorage> storage = new MsgPacked<>(in.readByteArray(), WaypointStorage.class);
            return new RemoveWaypointChange(storage);
        }
    }
}
