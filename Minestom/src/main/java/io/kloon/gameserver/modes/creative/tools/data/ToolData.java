package io.kloon.gameserver.modes.creative.tools.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.kloon.gameserver.util.gson.MinestomBlockGsonAdapter;
import io.kloon.gameserver.util.gson.ObjectIdGsonAdapter;
import net.minestom.server.instance.block.Block;
import org.bson.types.ObjectId;

public record ToolData<TItemBound, TPlayerBound>(
        TItemBound itemBound,
        TPlayerBound playerBound
) {
    public static Gson DATA_GSON = new GsonBuilder()
            .registerTypeAdapter(ObjectId.class, new ObjectIdGsonAdapter())
            .registerTypeHierarchyAdapter(Block.class, new MinestomBlockGsonAdapter())
            .create();
}
