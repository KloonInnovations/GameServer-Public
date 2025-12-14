package io.kloon.gameserver.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minestom.server.instance.block.Block;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MinestomBlockGsonAdapter extends TypeAdapter<Block> {
    @Override
    public void write(JsonWriter out, Block value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name("name").value(value.name());

        {
            out.name("props").beginObject();
            Map<String, String> properties = value.properties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                out.name(entry.getKey()).value(entry.getValue());
            }
            out.endObject();
        }

        out.endObject();
    }

    @Override
    public Block read(JsonReader in) throws IOException {
        Block block = null;
        Map<String, String> properties = new HashMap<>();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "name" -> {
                    block = Block.fromKey(in.nextString());
                }
                case "props" -> {
                    in.beginObject();
                    while(in.hasNext()) {
                        properties.put(in.nextName(), in.nextString());
                    }
                    in.endObject();
                }
                default -> in.skipValue();
            }
        }
        in.endObject();

        if (block == null) {
            return Block.STONE;
        }

        return block.withProperties(properties);
    }
}
