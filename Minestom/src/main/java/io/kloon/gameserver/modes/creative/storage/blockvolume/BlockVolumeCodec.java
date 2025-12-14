package io.kloon.gameserver.modes.creative.storage.blockvolume;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.storage.blockvolume.impl.BlockArray;
import io.kloon.gameserver.modes.creative.storage.blockvolume.impl.BlockEmpty;
import io.kloon.gameserver.modes.creative.storage.blockvolume.impl.BlockMap;

import java.io.IOException;

public class BlockVolumeCodec implements MinecraftCodec<BlockVolume> {
    public static final BlockVolumeCodec INSTANCE = new BlockVolumeCodec();

    private static final int VERSION = 2;

    @Override
    public void encode(BlockVolume obj, MinecraftOutputStream out) throws IOException {
        out.writeByte(VERSION);

        if (obj instanceof BlockEmpty empty) {
            out.writeByte(0);
        } else if (obj instanceof BlockArray blockArray) {
            out.writeByte(1);
            BlockArray.CODEC_V2.encode(blockArray, out);
        } else if (obj instanceof BlockMap blockMap) {
            out.writeByte(2);
            BlockMap.CODEC_V2.encode(blockMap, out);
        } else {
            throw new IllegalArgumentException(STR."Missing codec for \{obj.getClass().getName()}");
        }
    }

    @Override
    public BlockVolume decode(MinecraftInputStream in) throws IOException {
        int version = in.readByte();
        if (version > VERSION) throw new IllegalStateException(STR."Unsupported version \{version}, we are \{VERSION}");

        byte type = in.readByte();
        MinecraftCodec<? extends BlockVolume> codec;
        if (type == 0) {
            return new BlockEmpty();
        } else if (type == 1) {
            codec = switch (version) {
                case 1 -> BlockArray.CODEC_V1;
                case 2 -> BlockArray.CODEC_V2;
                default -> throw new IllegalStateException("Unexpected version: " + version);
            };
        } else if (type == 2) {
            codec = switch (version) {
                case 1 -> BlockMap.CODEC_V1;
                case 2 -> BlockMap.CODEC_V2;
                default -> throw new IllegalStateException("Unexpected version: " + version);
            };
        } else {
            throw new IllegalStateException(STR."Unknown volume type \{type}");
        }

        return codec.decode(in);
    }
}
