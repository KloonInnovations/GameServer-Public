package io.kloon.gameserver.chestmenus.signui;

import com.google.common.collect.Sets;
import io.kloon.gameserver.chestmenus.ChestMenuPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.instance.block.Block;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

public class SignUxBuilder {
    private Block signBlock = SignUX.DEFAULT_SIGN;
    private Component[] lines = Collections.nCopies(4, Component.empty()).toArray(Component[]::new);
    private Consumer<String[]> consumer = lines -> {};

    public SignUxBuilder sign(Block sign) {
        if (!SIGN_BLOCKS.contains(sign)) throw new IllegalStateException(STR."Unsupported sign type: \{sign}");
        this.signBlock = sign;
        return this;
    }

    public SignUxBuilder line(int index, String line) {
        lines[index] = Component.text(line);
        return this;
    }

    public SignUxBuilder line(int index, Component line) {
        lines[index] = line;
        return this;
    }

    public SignUxBuilder lines(String[] lines) {
        this.lines = Arrays.stream(lines).map(Component::text).toArray(Component[]::new);
        return this;
    }

    public SignUxBuilder lines(Component[] lines) {
        this.lines = Arrays.copyOf(lines, lines.length);
        return this;
    }

    public void display(ChestMenuPlayer player, Consumer<String[]> consumer) {
        SignUX.display(player, signBlock, lines, consumer);
    }

    private static final Set<Block> SIGN_BLOCKS = Sets.newHashSet(
            Block.OAK_SIGN,
            Block.SPRUCE_SIGN,
            Block.BIRCH_SIGN,
            Block.JUNGLE_SIGN,
            Block.ACACIA_SIGN,
            Block.DARK_OAK_SIGN,
            Block.MANGROVE_SIGN,
            Block.CHERRY_SIGN,
            Block.CRIMSON_SIGN,
            Block.WARPED_SIGN
    );
}
