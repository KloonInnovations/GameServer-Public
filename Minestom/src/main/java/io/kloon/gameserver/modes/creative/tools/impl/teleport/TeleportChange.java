package io.kloon.gameserver.modes.creative.tools.impl.teleport;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.io.codecs.MinestomCodecs;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.InstantResult;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

import java.io.IOException;

public class TeleportChange implements Change {
    private final Pos before;
    private final boolean flyBefore;

    private final Pos after;
    private final boolean flyAfter;

    public TeleportChange(Pos before, boolean flyBefore, Player player) {
        this.before = before;
        this.flyBefore = flyBefore;
        this.after = player.getPosition();
        this.flyAfter = player.isFlying();
    }

    public TeleportChange(Pos before, boolean flyBefore, Pos after, boolean flyAfter) {
        this.before = before;
        this.flyBefore = flyBefore;
        this.after = after;
        this.flyAfter = flyAfter;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.TELEPORT;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        ctx.player().teleport(before);
        return new InstantResult();
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        ctx.player().teleport(after);
        return new InstantResult();
    }

    public static final Codec CODEC = new Codec();
    public static final class Codec implements MinecraftCodec<TeleportChange> {
        @Override
        public void encode(TeleportChange obj, MinecraftOutputStream out) throws IOException {
            out.write(obj.before, MinestomCodecs.POS);
            out.writeBoolean(obj.flyBefore);
            out.write(obj.after, MinestomCodecs.POS);
            out.writeBoolean(obj.flyAfter);
        }

        @Override
        public TeleportChange decode(MinecraftInputStream in) throws IOException {
            return new TeleportChange(
                    in.read(MinestomCodecs.POS),
                    in.readBoolean(),
                    in.read(MinestomCodecs.POS),
                    in.readBoolean()
            );
        }
    }
}
