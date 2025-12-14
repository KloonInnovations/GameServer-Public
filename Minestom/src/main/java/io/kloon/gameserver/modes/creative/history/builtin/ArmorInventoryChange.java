package io.kloon.gameserver.modes.creative.history.builtin;

import io.kloon.gameserver.minestom.armor.ArmorSlot;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.ExceptionResult;
import io.kloon.gameserver.modes.creative.history.results.InstantResult;
import io.kloon.gameserver.modes.creative.storage.datainworld.util.Zstded;
import io.kloon.gameserver.modes.creative.storage.inventories.CreativeInvDecoderV1;
import io.kloon.gameserver.modes.creative.storage.inventories.CreativeInvEncoderV1;
import net.minestom.server.item.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArmorInventoryChange implements Change {
    private final Zstded<List<ItemStack>> before;
    private final Zstded<List<ItemStack>> after;

    public ArmorInventoryChange(ItemStack[] before, ItemStack[] after) {
        this(Arrays.asList(before), Arrays.asList(after));
    }

    public ArmorInventoryChange(List<ItemStack> before, List<ItemStack> after) {
        CreativeInvEncoderV1 encoder = new CreativeInvEncoderV1();
        this.before = new Zstded<>(before, encoder);
        this.after = new Zstded<>(after, encoder);
    }

    private ArmorInventoryChange(Zstded<List<ItemStack>> before, Zstded<List<ItemStack>> after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.ARMOR_INVENTORY;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        return applyArmor(ctx.player(), before);
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        return applyArmor(ctx.player(), after);
    }

    private ChangeResult applyArmor(CreativePlayer player, Zstded<List<ItemStack>> zstded) {
        List<ItemStack> items;
        try {
            items = zstded.read(new CreativeInvDecoderV1(player));
        } catch (Throwable t) {
            return new ExceptionResult(t);
        }

        ArmorSlot[] slots = ArmorSlot.VALUES;
        for (int i = 0; i < Math.min(items.size(), slots.length); ++i) {
            ArmorSlot slot = slots[i];
            ItemStack item = items.get(i);
            slot.set(player, item);
        }

        return new InstantResult();
    }

    public static ItemStack[] getArmor(CreativePlayer player) {
        ItemStack[] items = new ItemStack[ArmorSlot.VALUES.length];
        for (int i = 0; i < ArmorSlot.VALUES.length; ++i) {
            ArmorSlot slot = ArmorSlot.VALUES[i];
            ItemStack item = slot.get(player);
            items[i] = item;
        }
        return items;
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<ArmorInventoryChange> {
        @Override
        public void encode(ArmorInventoryChange obj, MinecraftOutputStream out) throws IOException {
            out.write(obj.before, Zstded.CODEC);
            out.write(obj.after, Zstded.CODEC);
        }

        @Override
        public ArmorInventoryChange decode(MinecraftInputStream in) throws IOException {
            return new ArmorInventoryChange(
                    in.read(Zstded.CODEC),
                    in.read(Zstded.CODEC)
            );
        }
    }
}
