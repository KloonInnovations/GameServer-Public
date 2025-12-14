package io.kloon.gameserver.modes.creative.tools.impl.waypoint.changes;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.io.codecs.MinestomCodecs;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.InstantResult;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointColor;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointsStorage;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.WaypointEntity;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;

import java.io.IOException;
import java.util.UUID;

public class AddWaypointChange implements Change {
    private final UUID uuid;

    private final UUID ownerUuid;
    private final String ownerName;

    private final WaypointColor color;
    private final String name;
    private final Pos pos;

    public AddWaypointChange(WaypointStorage basedOn) {
        this.uuid = basedOn.getUuid();
        this.ownerUuid = basedOn.getOwnerId();
        this.ownerName = basedOn.getOwnerName();

        this.color = basedOn.getColor();
        this.name = basedOn.getName();
        this.pos = basedOn.getPosition();
    }

    public AddWaypointChange(UUID uuid, UUID ownerUuid, String ownerName, WaypointColor color, String name, Pos pos) {
        this.uuid = uuid;
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
        this.color = color;
        this.name = name;
        this.pos = pos;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.ADD_WAYPOINT;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        CreativeInstance instance = ctx.instance();
        ensureDelete(instance, uuid);
        return new InstantResult();
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        CreativeInstance instance = ctx.instance();
        ensureDelete(instance, uuid);

        WaypointsStorage waypoints = instance.getWorldStorage().getWaypoints();

        WaypointStorage waypointStorage = new WaypointStorage(uuid, pos, ownerUuid, ownerName)
                .withColor(color)
                .withName(name);
        waypoints.add(waypointStorage);

        WaypointEntity.spawn(instance, waypointStorage);

        return new InstantResult();
    }

    private void ensureDelete(CreativeInstance instance, UUID waypointUuid) {
        WaypointsStorage storage = instance.getWorldStorage().getWaypoints();
        storage.removeByUuid(waypointUuid);
        Entity waypointEntity = instance.getEntityByUuid(uuid);
        if (waypointEntity != null) {
            waypointEntity.remove();
        }
    }

    public static final Codec CODEC = new Codec();
    public static final class Codec implements MinecraftCodec<AddWaypointChange> {
        @Override
        public void encode(AddWaypointChange obj, MinecraftOutputStream out) throws IOException {
            out.writeUuid(obj.uuid);
            out.writeUuid(obj.ownerUuid);
            out.writeString(obj.ownerName);
            out.writeString(obj.color.getDbKey());
            out.writeString(obj.name);
            out.write(obj.pos, MinestomCodecs.POS);
        }

        @Override
        public AddWaypointChange decode(MinecraftInputStream in) throws IOException {
            return new AddWaypointChange(
                    in.readUuid(),
                    in.readUuid(),
                    in.readString(),
                    WaypointColor.BY_DBKEY.get(in.readString(), WaypointColor.WHITE),
                    in.readString(),
                    in.read(MinestomCodecs.POS)
            );
        }
    }
}
