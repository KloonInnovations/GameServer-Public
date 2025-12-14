package io.kloon.gameserver.modes.creative.storage.datainworld;

import net.minestom.server.item.Material;

public enum CreativeWorldSize {
    SEVEN_X_SEVEN(Material.MOSSY_COBBLESTONE_SLAB,  "Small", 3),
    ELEVEN_X_ELEVEN(Material.MOSSY_COBBLESTONE_STAIRS, "Medium", 5),
    FIFTEEN_X_FIFTEEN(Material.MOSSY_COBBLESTONE_WALL, "Large", 7),
    NINETEEN_X_NINETEEN(Material.MOSSY_COBBLESTONE, "X-Large", 9)
    ;

    private final Material icon;
    private final String cuteName;
    private final int chunksRadius;

    CreativeWorldSize(Material icon, String cuteName, int chunksRadius) {
        this.icon = icon;
        this.cuteName = cuteName;
        this.chunksRadius = chunksRadius;
    }

    public Material getIcon() {
        return icon;
    }

    public String getCuteName() {
        return cuteName;
    }

    public int getChunksRadius() {
        return chunksRadius;
    }

    public int getChunksDiameter() {
        return chunksRadius * 2 + 1;
    }
}
