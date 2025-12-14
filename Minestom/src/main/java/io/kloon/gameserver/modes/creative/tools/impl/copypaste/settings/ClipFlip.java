package io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings;

import io.kloon.gameserver.minestom.blocks.transforms.FlipTransform;

import java.util.HashSet;
import java.util.Set;

public record ClipFlip(
        boolean x,
        boolean y,
        boolean z
) {
    public boolean none() {
        return !(x || y || z);
    }

    public Set<FlipTransform> toTransforms() {
        Set<FlipTransform> transforms = new HashSet<>();
        if (x) {
            transforms.add(FlipTransform.FRONT_BACK);
        }
        if (z) {
            transforms.add(FlipTransform.LEFT_RIGHT);
        }
        return transforms;
    }
}
