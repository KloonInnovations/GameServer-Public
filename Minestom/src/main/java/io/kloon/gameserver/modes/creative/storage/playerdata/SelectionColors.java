package io.kloon.gameserver.modes.creative.storage.playerdata;

import io.kloon.gameserver.minestom.color.ColorUtils;
import io.kloon.infra.mongo.storage.BufferedDocument;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import net.minestom.server.color.Color;
import org.bson.Document;

import java.util.Random;
import java.util.UUID;

public class SelectionColors {
    private final BufferedDocument document;

    private Color noSelection;
    private Color oneSelection;
    private Color fullSelection;

    public SelectionColors(UUID playerId, BufferedDocument document) {
        this.document = document;

        if (!document.containsKey(FULL_SELECTION)) {
            replaceAll(generate(playerId));
        }

        this.noSelection = new Color(document.getInt(NO_SELECTION));
        this.oneSelection = new Color(document.getInt(ONE_SELECTION));
        this.fullSelection = new Color(document.getInt(FULL_SELECTION));
    }

    public SelectionColors(Color noSelection, Color oneSelection, Color fullSelection) {
        this.document = new BufferedDocument(new Document());
        setNoSelection(noSelection);
        setOneSelection(oneSelection);
        setFullSelection(fullSelection);
    }

    public Color getNoSelection() {
        return noSelection;
    }

    public String getNoSelectionHex() {
        return TextColor.color(noSelection).asHexString();
    }

    public void setNoSelection(Color color) {
        this.noSelection = color;
        document.putInt(NO_SELECTION, color.asRGB());
    }

    public Color getOneSelection() {
        return oneSelection;
    }

    public String getOneSelectionHex() {
        return TextColor.color(oneSelection).asHexString();
    }

    public void setOneSelection(Color color) {
        this.oneSelection = color;
        document.putInt(ONE_SELECTION, color.asRGB());
    }

    public Color getFullSelection() {
        return fullSelection;
    }

    public String getFullSelectionHex() {
        return TextColor.color(fullSelection).asHexString();
    }

    public void setFullSelection(Color color) {
        this.fullSelection = color;
        document.putInt(FULL_SELECTION, color.asRGB());
    }

    public void replaceAll(SelectionColors newColors) {
        setNoSelection(newColors.noSelection);
        setOneSelection(newColors.oneSelection);
        setFullSelection(newColors.fullSelection);
    }

    private static final String NO_SELECTION = "none";
    private static final String ONE_SELECTION = "one";
    private static final String FULL_SELECTION = "full";

    public static SelectionColors generate(UUID playerId) {
        Random rand = new Random(playerId.hashCode());
        float h = rand.nextFloat();
        float s = rand.nextFloat(0.84f, 1.0f);
        float v = rand.nextFloat(0.78f, 1.0f);
        HSVLike main = HSVLike.hsvLike(h, s, v);

        return generate(main);
    }

    public static SelectionColors generate(HSVLike main) {
        float invertH = (main.h() + (2f/3)) % 1.0f;
        HSVLike invert = HSVLike.hsvLike(invertH, main.s() - 0.015f, main.v() - 0.04f);

        HSVLike pale = HSVLike.hsvLike(main.h(), 0.135f, main.v());

        return new SelectionColors(
                ColorUtils.hsvToColor(pale),
                ColorUtils.hsvToColor(invert),
                ColorUtils.hsvToColor(main));
    }
}
