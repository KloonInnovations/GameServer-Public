package io.kloon.gameserver.modes.creative.tools.snipe.settings;

import io.kloon.infra.mongo.storage.BufferedDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SnipePlayerStorage {
    private final BufferedDocument document;

    private double range;
    private boolean ignoreBlocks;
    private List<Double> rangeAnchors;

    public static final double DEFAULT_RANGE = 7.0;
    public static final double MAX_RANGE = 80.0;
    public static final int MAX_ANCHORS = 7;
    private static final List<Double> DEFAULT_ANCHORS = Arrays.asList(5.0, 12.0, 25.0, 50.0);

    public SnipePlayerStorage(BufferedDocument document) {
        this.document = document;

        this.range = document.getDouble(RANGE, DEFAULT_RANGE);
        this.ignoreBlocks = document.getBoolean(IGNORE_BLOCKS, false);

        this.rangeAnchors = document.getArrayDouble(RANGE_ANCHORS).collect(Collectors.toList());
        if (rangeAnchors.isEmpty()) {
            rangeAnchors.addAll(DEFAULT_ANCHORS);
        }
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
        document.putDouble(RANGE, range);
    }

    public boolean isIgnoreBlocks() {
        return ignoreBlocks;
    }

    public void setIgnoreBlocks(boolean ignoreBlocks) {
        this.ignoreBlocks = ignoreBlocks;
        document.putBoolean(IGNORE_BLOCKS, ignoreBlocks);
    }

    public boolean isRangeShortcutEnabled() {
        return document.getBoolean(RANGE_SHORTCUT_ENABLED, true);
    }

    public void setRangeShortcutEnabled(boolean enabled) {
        document.putBoolean(RANGE_SHORTCUT_ENABLED, enabled);
    }

    public List<Double> getRangeAnchors() {
        return new ArrayList<>(rangeAnchors);
    }

    public void setRangeAnchors(List<Double> updated) {
        this.rangeAnchors = new ArrayList<>(updated);
        Collections.sort(this.rangeAnchors);
        document.putArrayDouble(RANGE_ANCHORS, rangeAnchors);
    }

    public SnipeVisibility getShapeVisibility() {
        String visibilityStr = document.getString(SHAPE_VISBILITY);
        return SnipeVisibility.BY_DB_KEY.get(visibilityStr, SnipeVisibility.GLOWING);
    }

    public void setShapeVisibility(SnipeVisibility visibility) {
        document.putString(SHAPE_VISBILITY, visibility.getDbKey());
    }

    private static final String RANGE = "range";
    private static final String IGNORE_BLOCKS = "ignore_blocks";
    private static final String RANGE_SHORTCUT_ENABLED = "range_shortcut_enabled";
    private static final String RANGE_ANCHORS = "range_anchors";
    private static final String SHAPE_VISBILITY = "shape_visibility";
}
