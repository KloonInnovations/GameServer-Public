package io.kloon.gameserver.modes.creative.tools.impl.erosion;

import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.menu.preferences.common.radius.RadiusSettings;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.params.ErosionParams;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.params.ErosionPreset;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.work.ErosionGen;
import net.minestom.server.coordinate.BlockVec;

public class ErosionToolSettings implements RadiusSettings {
    private ErosionParams params = ErosionPreset.LIFT.getErosionParams();
    private int radius = 5;

    public ErosionParams getParams() {
        return params;
    }

    public void setParams(ErosionParams params) {
        this.params = params;
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public ErosionGen createGenSettings(BlockVec center, boolean sphere, boolean reverseParams, MaskLookup mask) {
        ErosionParams params = this.params;
        if (reverseParams) {
            params = params.reverse();
        }
        return new ErosionGen(center, mask, radius, sphere, params);
    }
}
