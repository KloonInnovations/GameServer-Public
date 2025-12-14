package io.kloon.gameserver.modes.creative.history.builtin;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.ChangeContext;
import io.kloon.gameserver.modes.creative.history.ChangeRecord;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;
import io.kloon.gameserver.modes.creative.history.results.ExceptionResult;
import io.kloon.gameserver.modes.creative.history.results.InstantResult;
import io.kloon.gameserver.modes.creative.storage.datainworld.util.Zstded;
import io.kloon.gameserver.modes.creative.storage.inventories.CreativeInvDecoderV1;
import io.kloon.gameserver.modes.creative.storage.inventories.CreativeInvEncoderV1;
import net.minestom.server.item.ItemStack;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FullInventoryChange implements Change {
    private final Zstded<List<ItemStack>> before;
    private final Zstded<List<ItemStack>> after;

    public FullInventoryChange(ItemStack[] before, ItemStack[] after) {
        this(Arrays.asList(before), Arrays.asList(after));
    }

    public FullInventoryChange(List<ItemStack> before, List<ItemStack> after) {
        CreativeInvEncoderV1 encoder = new CreativeInvEncoderV1();
        this.before = new Zstded<>(before, encoder);
        this.after = new Zstded<>(after, encoder);
    }

    private FullInventoryChange(Zstded<List<ItemStack>> before, Zstded<List<ItemStack>> after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public ChangeType getType() {
        return ChangeType.FULL_INVENTORY;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        return applyInv(ctx.player(), before);
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        return applyInv(ctx.player(), after);
    }

    private ChangeResult applyInv(CreativePlayer player, Zstded<List<ItemStack>> zstded) {
        List<ItemStack> items;
        try {
            items = zstded.read(new CreativeInvDecoderV1(player));
        } catch (Throwable t) {
            return new ExceptionResult(t);
        }

        player.getInventory().copyContents(items.toArray(ItemStack[]::new));

        return new InstantResult();
    }

    public static final Codec CODEC = new Codec();
    public static final class Codec implements MinecraftCodec<FullInventoryChange> {
        @Override
        public void encode(FullInventoryChange obj, MinecraftOutputStream out) throws IOException {
            out.write(obj.before, Zstded.CODEC);
            out.write(obj.after, Zstded.CODEC);
        }

        @Override
        public FullInventoryChange decode(MinecraftInputStream in) throws IOException {
            return new FullInventoryChange(
                    in.read(Zstded.CODEC),
                    in.read(Zstded.CODEC)
            );
        }
    }
}
