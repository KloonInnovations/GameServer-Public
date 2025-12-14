package io.kloon.gameserver.modes.creative.storage.datainworld.util;

import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;

import java.io.IOException;

public class MsgPacked<T> {
    private byte[] serialized;
    private final Class<T> clazz;

    public MsgPacked(T obj, Class<T> clazz) {
        try {
            this.serialized = CreativeWorldStorage.MSG_PACK.writeValueAsBytes(obj);
        } catch (Throwable t) {
            throw new RuntimeException("Error serializing", t);
        }
        this.clazz = clazz;
    }

    public MsgPacked(byte[] serialized, Class<T> clazz) {
        this.serialized = serialized;
        this.clazz = clazz;
    }

    public byte[] getSerialized() {
        return serialized;
    }

    public T read() throws IOException {
        return CreativeWorldStorage.MSG_PACK.readValue(serialized, clazz);
    }
}
