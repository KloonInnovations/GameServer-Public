package io.kloon.gameserver.modes.creative.tools.impl.erosion.params;

import io.kloon.gameserver.util.WordUtilsK;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.item.Material;

public enum ErosionPreset {
    MELT(Material.LAVA_BUCKET, "Removes landscape, like an ice cream cone in the summer.",
            new ErosionParams(2, 1, 5, 1)),

    FILL(Material.IRON_SHOVEL, "Fill edges and holes, the reverse of melt.",
            new ErosionParams(5, 1, 2, 1)),

    SMOOTH(Material.IRON_HOE, "Refines edges or remove unwanted features.",
            new ErosionParams(3, 1, 3, 1)),

    LIFT(Material.ELYTRA, "Raise all blocks by 1.",
            new ErosionParams(0, 0, 1, 1)),

    LOWER(Material.POLISHED_BLACKSTONE_BRICK_SLAB, "The reverse of lift.",
            new ErosionParams(1, 1, 0, 0)),

    FLOATCLEAN(Material.WARPED_BUTTON, "Removes floating blocks and fills surrounded empty spaces.",
            new ErosionParams(6, 1, 6, 1)),
    ;

    private final String name;
    private final Material icon;
    private final String description;
    private final ErosionParams params;

    ErosionPreset(Material icon, String description, ErosionParams params) {
        this.icon = icon;
        this.description = description;
        this.name = WordUtilsK.enumName(this);
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public ErosionParams getErosionParams() {
        return params;
    }

    public boolean matches(ErosionParams params) {
        return this.params.equals(params);
    }

    public static EnumQuery<ErosionParams, ErosionPreset> BY_PARAMS = new EnumQuery<>(values(), p -> p.params);
}
