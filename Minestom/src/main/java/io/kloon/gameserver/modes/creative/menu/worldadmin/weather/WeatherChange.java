package io.kloon.gameserver.modes.creative.menu.worldadmin.weather;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.InstantResult;
import io.kloon.gameserver.modes.creative.storage.datainworld.world.CreativeWeatherStorage;
import net.minestom.server.instance.Weather;

import java.io.IOException;

public class WeatherChange implements Change {
    private final Weather before;
    private final Weather after;

    public WeatherChange(Weather before, Weather after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.WEATHER;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        return setWeather(ctx.instance(), before);
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        return setWeather(ctx.instance(), after);
    }

    private ChangeResult setWeather(CreativeInstance instance, Weather weather) {
        instance.setWeather(weather, 1);

        CreativeWeatherStorage weatherStorage = instance.getWorldStorage().getWeather();
        weatherStorage.setRainLevel(weather.rainLevel());
        weatherStorage.setThunderLevel(weather.rainLevel());

        return new InstantResult();
    }

    public static final Codec CODEC = new Codec();
    public static final class Codec implements MinecraftCodec<WeatherChange> {
        @Override
        public void encode(WeatherChange change, MinecraftOutputStream out) throws IOException {
            out.write(change.before, WEATHER_CODEC);
            out.write(change.after, WEATHER_CODEC);
        }

        @Override
        public WeatherChange decode(MinecraftInputStream in) throws IOException {
            return new WeatherChange(
                    in.read(WEATHER_CODEC),
                    in.read(WEATHER_CODEC)
            );
        }
    }

    public static final WeatherCodec WEATHER_CODEC = new WeatherCodec();
    public static final class WeatherCodec implements MinecraftCodec<Weather> {
        @Override
        public void encode(Weather weather, MinecraftOutputStream out) throws IOException {
            out.writeFloat(weather.rainLevel());
            out.writeFloat(weather.thunderLevel());
        }

        @Override
        public Weather decode(MinecraftInputStream in) throws IOException {
            return new Weather(
                    in.readFloat(),
                    in.readFloat()
            );
        }
    }
}
