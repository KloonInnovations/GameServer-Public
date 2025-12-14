package io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.sign;

import io.kloon.gameserver.minestom.nbt.KloonCompoundBuilder;
import io.kloon.gameserver.minestom.nbt.NBT;
import io.kloon.gameserver.minestom.nbt.NbtCodec;
import net.kyori.adventure.nbt.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SignTileText {
    private boolean glowingText = false;
    private NamedTextColor color = NamedTextColor.BLACK;
    private Component[] lines = Collections.nCopies(4, Component.empty()).toArray(Component[]::new);

    public SignTileText withGlowing(boolean glowingText) {
        this.glowingText = glowingText;
        return this;
    }

    public boolean glowing() {
        return glowingText;
    }

    public SignTileText withColor(NamedTextColor color) {
        this.color = color;
        return this;
    }

    public NamedTextColor color() {
        return color;
    }

    public SignTileText withLines(Component[] lines) {
        this.lines = lines;
        return this;
    }

    public SignTileText withLines(String[] lines) {
        for (int i = 0; i < Math.min(4, lines.length); ++i) {
            this.lines[i] = Component.text(lines[i]);
        }
        return this;
    }

    public SignTileText withLine(int index, Component line) {
        lines[index] = line;
        return this;
    }

    public SignTileText withLine(int index, String line) {
        lines[index] = Component.text(line);
        return this;
    }

    public Component[] lines() {
        return lines;
    }

    public CompoundBinaryTag toNBT() {
        GsonComponentSerializer serializer = GsonComponentSerializer.gson();
        List<String> messages = Arrays.stream(lines).map(serializer::serialize).toList();
        return NBT.compound(c -> {
            c.putByte("has_glowing_text", glowingText ? 1 : 0);
            c.putString("color", color.toString());
            c.putStringList("messages", messages);
        });
    }

    public static final Codec NBT_CODEC = new Codec();
    public static final class Codec implements NbtCodec<SignTileText> {
        @Override
        public void encode(SignTileText obj, KloonCompoundBuilder c) {
            c.putBoolean("has_glowing_text", obj.glowingText);
            c.putString("color", obj.color.toString());

            GsonComponentSerializer serializer = GsonComponentSerializer.gson();
            List<String> messages = Arrays.stream(obj.lines).map(serializer::serialize).toList();
            c.putStringList("messages", messages);
        }

        @Override
        public SignTileText decode(@Nullable CompoundBinaryTag c) {
            SignTileText text = new SignTileText();
            if (c == null) {
                return text;
            }

            text.withGlowing(c.getBoolean("has_glowing_text", false));

            String colorStr = c.getString("color", "black");
            NamedTextColor color = NamedTextColor.NAMES.value(colorStr);
            text.withColor(color);

            GsonComponentSerializer serializer = GsonComponentSerializer.gson();
            ListBinaryTag messages = c.getList("messages", BinaryTagTypes.STRING);
            Component[] lines = messages.stream()
                    .map(tag -> ((StringBinaryTag) tag).value())
                    .map(serializer::deserialize)
                    .toArray(Component[]::new);
            text.withLines(lines);

            return text;
        }
    }
}
