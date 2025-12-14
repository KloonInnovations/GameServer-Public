package io.kloon.gameserver.modes.creative.menu.worldadmin.time;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.InstantResult;
import io.kloon.gameserver.modes.creative.storage.datainworld.world.CreativeTimeStorage;

import java.io.IOException;

public class TimeChange implements Change {
    private final Time before;
    private final Time after;

    public TimeChange(Time before, Time after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.TIME;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        return setTime(ctx.instance(), before);
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        return setTime(ctx.instance(), after);
    }

    private ChangeResult setTime(CreativeInstance instance, Time time) {
        instance.setTime((long) time.time);

        CreativeTimeStorage timeStorage = instance.getWorldStorage().getTime();
        timeStorage.setTime(time.time);
        timeStorage.setTimeRate(time.timeRate);

        return new InstantResult();
    }

    public record Time(double time, double timeRate) {}

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<TimeChange> {
        @Override
        public void encode(TimeChange change, MinecraftOutputStream out) throws IOException {
            out.write(change.before, TIME_CODEC);
            out.write(change.after, TIME_CODEC);
        }

        @Override
        public TimeChange decode(MinecraftInputStream in) throws IOException {
            return new TimeChange(
                    in.read(TIME_CODEC),
                    in.read(TIME_CODEC)
            );
        }
    }

    private static final TimeCodec TIME_CODEC = new TimeCodec();
    private static class TimeCodec implements MinecraftCodec<Time> {
        @Override
        public void encode(Time time, MinecraftOutputStream out) throws IOException {
            out.writeDouble(time.time);
            out.writeDouble(time.timeRate);
        }

        @Override
        public Time decode(MinecraftInputStream in) throws IOException {
            return new Time(
                    in.readDouble(),
                    in.readDouble()
            );
        }
    }
}
