package io.kloon.gameserver.minestom.nbt;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public final class NBT {
    private NBT() {}

    public static CompoundBinaryTag compound(Consumer<KloonCompoundBuilder> editor) {
        KloonCompoundBuilder builder = new KloonCompoundBuilder();
        editor.accept(builder);
        return builder.build();
    }

    public static <T> CompoundBinaryTag compound(T obj, NbtCodec<T> codec) {
        return compound(c -> codec.encode(obj, c));
    }

    public static Tag<List<ItemStack>> SlottedItems(String key) {
        Tag<List<ItemStack>> regular = Tag.ItemStack(key).list();
        Tag<BinaryTag> slotted = Tag.NBT(key);
        return Tag.View(new TagSerializer<>() {
            @Override
            public @Nullable List<ItemStack> read(@NotNull TagReadable reader) {
                return reader.getTag(regular);
            }

            @Override
            public void write(@NotNull TagWritable writer, @NotNull List<ItemStack> items) {
                ListBinaryTag.Builder<BinaryTag> listNbt = ListBinaryTag.builder();
                for (int slot = 0; slot < items.size(); slot++) {
                    ItemStack item = items.get(slot);
                    if (item == null) continue;
                    CompoundBinaryTag.Builder itemNbt = CompoundBinaryTag.builder();
                    itemNbt.put(item.toItemNBT());
                    itemNbt.putInt("Slot", slot);
                    listNbt.add(itemNbt.build());
                }
                writer.setTag(slotted, listNbt.build());
            }
        });
    }
}
