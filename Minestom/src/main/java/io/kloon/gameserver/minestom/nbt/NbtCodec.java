package io.kloon.gameserver.minestom.nbt;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.Nullable;

public interface NbtCodec<T> {
    void encode(T obj, KloonCompoundBuilder c);

    T decode (@Nullable CompoundBinaryTag c);
}
