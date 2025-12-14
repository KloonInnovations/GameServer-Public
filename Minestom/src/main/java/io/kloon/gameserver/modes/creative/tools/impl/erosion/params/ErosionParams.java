package io.kloon.gameserver.modes.creative.tools.impl.erosion.params;

import org.jetbrains.annotations.Nullable;

public record ErosionParams(
        int erosionFaces,
        int erosionIterations,
        int fillFaces,
        int fillIterations
) {
    public ErosionParams withErosionFaces(int erosionFaces) {
        return new ErosionParams(erosionFaces, erosionIterations, fillFaces, fillIterations);
    }

    public ErosionParams withErosionIterations(int erosionIterations) {
        return new ErosionParams(erosionFaces, erosionIterations, fillFaces, fillIterations);
    }

    public ErosionParams withFillFaces(int fillFaces) {
        return new ErosionParams(erosionFaces, erosionIterations, fillFaces, fillIterations);
    }

    public ErosionParams withFillIterations(int fillIterations) {
        return new ErosionParams(erosionFaces, erosionIterations, fillFaces, fillIterations);
    }

    public ErosionParams reverse() {
        return new ErosionParams(fillFaces, fillIterations, erosionFaces, erosionIterations);
    }

    @Nullable
    public ErosionPreset getPreset() {
        return ErosionPreset.BY_PARAMS.get(this);
    }
}
