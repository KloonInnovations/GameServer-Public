package io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.sign;

import io.kloon.gameserver.minestom.nbt.KloonCompoundBuilder;
import io.kloon.gameserver.minestom.nbt.NBT;
import io.kloon.gameserver.minestom.nbt.NbtCodec;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SignTile {
    private boolean waxed = false;
    private SignTileText front = new SignTileText();
    private SignTileText back = new SignTileText();

    public SignTile waxed(boolean waxed) {
        this.waxed = waxed;
        return this;
    }

    public boolean isWaxed() {
        return waxed;
    }

    public SignTile withFront(SignTileText text) {
        this.front = text;
        return this;
    }

    public SignTile withFront(Consumer<SignTileText> editor) {
        editor.accept(front);
        return this;
    }

    public SignTileText front() {
        return front;
    }

    public SignTile withBack(SignTileText text) {
        this.back = text;
        return this;
    }

    public SignTile withBack(Consumer<SignTileText> editor) {
        editor.accept(back);
        return this;
    }

    public SignTileText back() {
        return back;
    }

    public SignTileText side(SignSide side) {
        return side == SignSide.FRONT ? front() : back();
    }

    public SignTile withSide(SignSide side, SignTileText text) {
        return side == SignSide.FRONT ? withFront(text) : withBack(text);
    }

    public CompoundBinaryTag toNBT() {
        return NBT.compound(c -> {
            c.putBoolean("is_waxed", waxed);
            c.put("front_text", front.toNBT());
            c.put("back_text", back.toNBT());
        });
    }

    public static final Codec NBT_CODEC = new Codec();
    public static class Codec implements NbtCodec<SignTile> {
        @Override
        public void encode(SignTile obj, KloonCompoundBuilder c) {
            c.putBoolean("is_waxed", obj.waxed);
            c.putCompound("front_text", obj.front, SignTileText.NBT_CODEC);
            c.putCompound("back_text", obj.back, SignTileText.NBT_CODEC);
        }

        @Override
        public SignTile decode(@Nullable CompoundBinaryTag c) {
            SignTile tile = new SignTile();
            if (c == null) {
                return tile;
            }

            tile.waxed(c.getBoolean("is_waxed", false));
            tile.withFront(SignTileText.NBT_CODEC.decode(c.getCompound("front_text")));
            tile.withBack(SignTileText.NBT_CODEC.decode(c.getCompound("back_text")));

            return tile;
        }
    }
}