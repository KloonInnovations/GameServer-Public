package io.kloon.gameserver.util.serialization;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdMcCodec implements MinecraftCodec<ObjectId> {
    public static final ObjectIdMcCodec INSTANCE = new ObjectIdMcCodec();

    @Override
    public void encode(ObjectId obj, MinecraftOutputStream out) throws IOException {
        byte[] bytes = obj.toByteArray();
        out.write(bytes);
    }

    @Override
    public ObjectId decode(MinecraftInputStream in) throws IOException {
        byte[] bytes = new byte[12];
        int read = in.read(bytes);
        if (read != 12) throw new IOException("Missing ObjectId bytes");
        return new ObjectId(bytes);
    }
}
