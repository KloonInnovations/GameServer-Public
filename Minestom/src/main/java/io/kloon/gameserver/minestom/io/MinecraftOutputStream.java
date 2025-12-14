package io.kloon.gameserver.minestom.io;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MinecraftOutputStream extends DataOutputStream {
    private boolean networkNbt = true;

    public MinecraftOutputStream(OutputStream out) {
        super(out);
    }

    public void writeVarInt(int value) throws IOException {
        Leb128Utils.writeIntSigned(value, this);
    }

    public void writeVarLong(long value) throws IOException {
        Leb128Utils.writeLongSigned(value, this);
    }

    public void writeString(String str) throws IOException {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        writeVarInt(bytes.length);
        write(bytes);
    }

    public void writeComponent(Component component) throws IOException {
        String json = GsonComponentSerializer.gson().serialize(component);
        writeString(json);
    }

    public void writeUuid(UUID uuid) throws IOException {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    public void writeByteArray(byte[] bytes) throws IOException {
        writeVarInt(bytes.length);
        write(bytes);
    }

    public void writeBlockPosition(Point point) throws IOException {
        long longPos = (((long) point.x() & 0x3FFFFFF) << 38) |
                (((long) point.z() & 0x3FFFFFF) << 12) |
                ((long) point.y() & 0xFFF);
        writeLong(longPos);
    }

    public void writeNbt(CompoundBinaryTag nbt) throws IOException {
        BinaryTagIO.writer().writeNameless(nbt, (DataOutput) this);
    }

    public <T> void write(T obj, MinecraftEncoder<T> encoder) throws IOException {
        encoder.encode(obj, this);
    }

    public <T> void writeOptional(@Nullable T obj, MinecraftEncoder<T> encoder) throws IOException {
        if (obj == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);
            write(obj, encoder);
        }
    }

    public static <T> byte[] toBytes(T obj, MinecraftEncoder<T> encoder) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MinecraftOutputStream mcStream = new MinecraftOutputStream(bos);
        encoder.encode(obj, mcStream);
        mcStream.flush();
        return bos.toByteArray();
    }

    public static <T> byte[] toBytesSneaky(T obj, MinecraftEncoder<T> encoder) {
        try {
            return toBytes(obj, encoder);
        } catch (Throwable t) {
            throw new RuntimeException("Error encoding", t);
        }
    }
}
