package io.kloon.gameserver.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minestom.server.instance.block.Block;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinestomBlockGsonAdapterTest {
    @Test
    void test() {
        Block block = Block.ACACIA_DOOR
                .withProperty("half", "lower")
                .withProperty("hinge", "right");

        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Block.class, new MinestomBlockGsonAdapter())
                .create();

        String json = gson.toJson(block);
        System.out.println(json);

        Block back = gson.fromJson(json, Block.class);
        System.out.println(back);
    }
}