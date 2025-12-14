package io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl;

import io.kloon.gameserver.minestom.blocks.handlers.LecternBlock;
import io.kloon.gameserver.minestom.blocks.vanilla.tiles.InjectedNbt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public class LecternHandler implements BlockHandler, InjectedNbt {
    public static final Key ID = Key.key("lectern");

    public static final Tag<ItemStack> BOOK = Tag.ItemStack("Book");

    @Override
    public CompoundBinaryTag interjectNbt(Block block) {
        CompoundBinaryTag nbt = block.nbt();
        nbt = nbt == null ? CompoundBinaryTag.empty() : nbt;

        boolean hasBook = LecternBlock.HAS_BOOK.is(block);
        if (!hasBook) {
            return nbt;
        }

        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
        builder.put(nbt);

        BOOK.write(builder, ItemStack.of(Material.BOOK));

        return builder.build();
    }

    @Override
    public @NotNull Key getKey() {
        return ID;
    }
}
