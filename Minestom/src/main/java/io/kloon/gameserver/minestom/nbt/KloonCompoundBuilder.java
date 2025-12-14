package io.kloon.gameserver.minestom.nbt;

import net.kyori.adventure.nbt.*;

import java.util.ArrayList;
import java.util.List;

public final class KloonCompoundBuilder {
    private final CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();

    public KloonCompoundBuilder put(String key, BinaryTag tag) {
        builder.put(key, tag);
        return this;
    }

    public <T> KloonCompoundBuilder putCompound(String key, T obj, NbtCodec<T> codec) {
        CompoundBinaryTag tag = NBT.compound(obj, codec);
        builder.put(key, tag);
        return this;
    }

    public KloonCompoundBuilder putByte(String key, int value) {
        builder.putByte(key, (byte) value);
        return this;
    }

    public KloonCompoundBuilder putBoolean(String key, boolean value) {
        builder.putByte(key, (byte) (value ? 1 : 0));
        return this;
    }

    public KloonCompoundBuilder putFloat(String key, double value) {
        builder.putFloat(key, (float) value);
        return this;
    }

    public KloonCompoundBuilder putString(String key, String value) {
        builder.putString(key, value);
        return this;
    }

    public KloonCompoundBuilder putStringList(String key, List<String> values) {
        List<BinaryTag> tags = new ArrayList<>(values.size());
        for (String value : values) {
            tags.add(StringBinaryTag.stringBinaryTag(value));
        }
        builder.put(key, ListBinaryTag.listBinaryTag(BinaryTagTypes.STRING, tags));
        return this;
    }

    public KloonCompoundBuilder putFloatList(String key, List<Float> values) {
        List<BinaryTag> tags = new ArrayList<>(values.size());
        for (float value : values) {
            tags.add(FloatBinaryTag.floatBinaryTag(value));
        }
        builder.put(key, ListBinaryTag.listBinaryTag(BinaryTagTypes.FLOAT, tags));
        return this;
    }

    public KloonCompoundBuilder putCompoundList(String key, List<CompoundBinaryTag> values) {
        List<BinaryTag> tags = new ArrayList<>(values.size());
        tags.addAll(values);
        builder.put(key, ListBinaryTag.listBinaryTag(BinaryTagTypes.COMPOUND, tags));
        return this;
    }

    public CompoundBinaryTag build() {
        return builder.build();
    }
}
