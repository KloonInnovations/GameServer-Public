package io.kloon.gameserver.minestom.io;

import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.coordinate.BlockVec;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.UUID;

public class MinecraftInputStream extends DataInputStream {
    private boolean networkNbt = true;

    public MinecraftInputStream(byte[] bytes) {
        this(new ByteArrayInputStream(bytes));
    }

    public MinecraftInputStream(byte[] bytes, int offset, int length) {
        this(new ByteArrayInputStream(bytes, offset, length));
    }

    public MinecraftInputStream(InputStream in) {
        super(in);
    }

    public MinecraftInputStream withNetworkNbt(boolean networkNbt) {
        this.networkNbt = networkNbt;
        return this;
    }

    public int readVarInt() throws IOException {
        return Leb128Utils.readIntSigned(this);
    }

    public long readVarLong() throws IOException {
        return Leb128Utils.readLongSigned(this);
    }

    public String readString() throws IOException {
        int length = readVarInt();
        byte[] bytes = new byte[length];
        readFully(bytes);
        return new String(bytes);
    }

    public Component readComponent() throws IOException {
        String json = readString();
        return GsonComponentSerializer.gson().deserialize(json);
    }

    public UUID readUuid() throws IOException {
        return new UUID(readLong(), readLong());
    }

    public byte[] readByteArray() throws IOException {
        return readByteArray(Integer.MAX_VALUE);
    }

    public byte[] readByteArray(int maxLength) throws IOException {
        int length = readVarInt();
        if (length < 0) {
            throw new RuntimeException("Negative length for byte array");
        }
        if (length > maxLength) {
            throw new RuntimeException(STR."Too many bytes declared in byte array, wanted max \{maxLength}, got \{length}");
        }
        byte[] bytes = new byte[length];
        readFully(bytes);
        return bytes;
    }

    public BlockVec readBlockPosition() throws IOException {
        long longPos = readLong();
        final int x = (int) (longPos >> 38);
        final int y = (int) (longPos << 52 >> 52);
        final int z = (int) (longPos << 26 >> 38);
        return new BlockVec(x, y, z);
    }

    public CompoundBinaryTag readNbt() throws IOException {
        return BinaryTagIO.reader().readNameless((DataInput) this);
    }

    public <T> T read(MinecraftDecoder<T> codec) throws IOException {
        return codec.decode(this);
    }

    @Nullable
    public <T> T readOptional(MinecraftDecoder<T> codec) throws IOException {
        boolean present = readBoolean();
        return present ? codec.decode(this) : null;
    }

    public static <T> T fromBytes(byte[] bytes, MinecraftDecoder<T> decoder) throws IOException {
        MinecraftInputStream stream = new MinecraftInputStream(bytes);
        return decoder.decode(stream);
    }

    public static <T> T fromBytesSneaky(byte[] bytes, MinecraftDecoder<T> decoder) {
        try {
            MinecraftInputStream stream = new MinecraftInputStream(bytes);
            return decoder.decode(stream);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
