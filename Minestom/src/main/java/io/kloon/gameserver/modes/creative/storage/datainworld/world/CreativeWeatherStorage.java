package io.kloon.gameserver.modes.creative.storage.datainworld.world;

import net.minestom.server.instance.Weather;

public class CreativeWeatherStorage {
    private float rainLevel = 0f;
    private float thunderLevel = 0f;

    public float getRainLevel() {
        return rainLevel;
    }

    public void setRainLevel(float rainLevel) {
        this.rainLevel = rainLevel;
    }

    public float getThunderLevel() {
        return thunderLevel;
    }

    public void setThunderLevel(float thunderLevel) {
        this.thunderLevel = thunderLevel;
    }

    public Weather asWeather() {
        return new Weather(rainLevel, thunderLevel);
    }
}
