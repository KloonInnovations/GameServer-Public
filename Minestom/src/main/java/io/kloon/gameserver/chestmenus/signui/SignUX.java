package io.kloon.gameserver.chestmenus.signui;

import io.kloon.gameserver.chestmenus.ChestMenuPlayer;
import io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.sign.SignTile;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.client.play.ClientUpdateSignPacket;
import net.minestom.server.network.packet.server.play.BlockChangePacket;
import net.minestom.server.network.packet.server.play.BlockEntityDataPacket;
import net.minestom.server.network.packet.server.play.OpenSignEditorPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

public class SignUX {
    private static final Logger LOG = LoggerFactory.getLogger(SignUX.class);
    public static final Block DEFAULT_SIGN = Block.OAK_SIGN;

    private final UUID playerId;
    private final BlockVec blockPos;
    private final boolean front;
    private final Consumer<String[]> consumer;

    private boolean consumed = false;

    private SignUX(UUID playerId, BlockVec blockPos, boolean front, Consumer<String[]> consumer) {
        this.playerId = playerId;
        this.blockPos = blockPos;
        this.front = front;
        this.consumer = consumer;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void consume(ClientUpdateSignPacket packet) {
        if (consumed) return;
        consumed = true;

        if (!matches(packet)) {
            LOG.error(STR."Sign update mismatch for \{playerId}");
            return;
        }

        Player player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(playerId);
        if (player == null) return;

        Block block = player.getInstance().getBlock(blockPos);
        player.sendPacket(new BlockChangePacket(blockPos, block));

        try {
            String[] lines = packet.lines().toArray(String[]::new);
            consumer.accept(lines);
        } catch (Throwable t) {
            LOG.error("Error in sign UX consumer", t);
        }
    }

    private boolean matches(ClientUpdateSignPacket packet) {
        return blockPos.sameBlock(packet.blockPosition()) && front == packet.isFrontText();
    }

    public static SignUxBuilder builder() {
        return new SignUxBuilder();
    }

    public static void display(ChestMenuPlayer player, Consumer<String[]> consumer) {
        String[] lines = { "", "", "", "" };
        display(player, lines, consumer);
    }

    public static void display(ChestMenuPlayer player, String[] linesDisplay, Consumer<String[]> consumer) {
        display(player, DEFAULT_SIGN, linesDisplay, consumer);
    }

    public static void display(ChestMenuPlayer player, Block signBlock, String[] linesDisplay, Consumer<String[]> consumer) {
        Component[] components = Arrays.stream(linesDisplay)
                .map(Component::text)
                .toArray(Component[]::new);
        display(player, signBlock, components, consumer);
    }

    public static void display(ChestMenuPlayer player, Block signBlock, Component[] linesDisplay, Consumer<String[]> consumer) {
        Pos blockPos = player.getPosition().withY(y -> y - 6);
        player.sendPacket(new BlockChangePacket(blockPos, signBlock));

        CompoundBinaryTag signNbt = new SignTile().withFront(f -> f.withLines(linesDisplay)).toNBT();
        player.sendPacket(new BlockEntityDataPacket(blockPos, signBlock.registry().blockEntityId(), signNbt));

        player.sendPacket(new OpenSignEditorPacket(blockPos, true));

        SignUX signUX = new SignUX(player.getUuid(), new BlockVec(blockPos), true, consumer);
        player.setSignUX(signUX);
    }

    public static String[] inputLines(String thirdLine) {
        return inputLines(thirdLine, "");
    }

    public static String[] inputLines(String thirdLine, double min, double max) {
        return inputLines(thirdLine, min, max, NumberFmt.ONE_DECIMAL);
    }

    public static String[] inputLines(String thirdLine, double min, double max, NumberFormat nf) {
        return inputLines(thirdLine, STR."From \{nf.format(min)} to \{nf.format(max)}");
    }

    public static final String UNDERLINE = "^^^^^^";

    public static String[] inputLines(String thirdLine, String fourthLine) {
        return new String[] {
                "",
                UNDERLINE,
                thirdLine,
                fourthLine
        };
    }
}
