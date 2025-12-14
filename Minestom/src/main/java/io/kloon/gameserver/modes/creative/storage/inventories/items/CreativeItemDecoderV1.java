package io.kloon.gameserver.modes.creative.storage.inventories.items;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftDecoder;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.MasksUnion;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.blocks.PatternBlock;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.BlockCodecNoTile;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolsListener;
import io.kloon.gameserver.modes.creative.tools.generics.NotImplementedTool;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.color.Color;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.modes.creative.storage.inventories.CreativeInventoryCodecV1.*;

public class CreativeItemDecoderV1 implements MinecraftDecoder<ItemStack> {
    private final ToolsListener tools;
    private final CreativePlayer player;

    public CreativeItemDecoderV1(CreativePlayer player) {
        this.tools = player.getCreative().getToolsListener();
        this.player = player;
    }

    @Override
    public ItemStack decode(MinecraftInputStream in) throws IOException {
        byte itemType = in.readByte();
        return switch (itemType) {
            case ITEM_TYPE_AIR -> ItemStack.AIR;
            case ITEM_TYPE_TOOL -> decodeTool(in);
            case ITEM_TYPE_NBT -> readRegularItem(in);
            case ITEM_TYPE_TINKERED_BLOCK -> readTinkeredBlock(in);
            case ITEM_TYPE_PATTERN_BLOCK -> readPatternBlock(in);
            case ITEM_TYPE_MASK -> readMask(in);
            default -> throw new IllegalStateException(STR."Unexpected itemType byte: \{itemType}");
        };
    }

    private ItemStack decodeTool(DataInputStream stream) throws IOException {
        String toolTypeDbKey = stream.readUTF();

        CreativeToolType toolType = CreativeToolType.BY_DBKEY.get(toolTypeDbKey, null);
        String toolData = stream.readBoolean() ? stream.readUTF() : null;

        CreativeTool tool = toolType == null
                ? new NotImplementedTool(toolTypeDbKey, toolData)
                : tools.get(toolType);

        Object itemBound = tool.getItemBound(toolData);
        Object playerBound = tool.getPlayerBound(player);

        return tool.renderItem(itemBound, playerBound);
    }

    private ItemStack readRegularItem(DataInputStream stream) throws IOException {
        try {
            CompoundBinaryTag itemNbt = BinaryTagTypes.COMPOUND.read(stream);
            return ItemStack.fromItemNBT(itemNbt);
        } catch (Throwable t) {
            throw new IOException("Error decoding not-tool", t);
        }
    }

    private ItemStack readTinkeredBlock(MinecraftInputStream stream) throws IOException {
        byte type = stream.readByte();
        if (type == 0) {
            return readRegularItem(stream);
        }

        MinecraftCodec<Block> codec = type == 2
                ? DumbPalette.BLOCK_CODEC_FULL
                : DumbPalette.BLOCK_CODEC_NO_TILE;

        Block block = codec.decode(stream);
        TinkeredBlock tinkered = new TinkeredBlock(block);
        ItemStack item = tinkered.toItem();
        if (item == null) {
            throw new IOException("Error reading tinkered block");
        }
        return item;
    }

    private ItemStack readPatternBlock(MinecraftInputStream stream) throws IOException {
        byte type = stream.readByte();
        if (type == 0) {
            return readRegularItem(stream);
        }

        CreativePattern pattern = stream.read(CreativePattern.CODEC);
        PatternBlock patternBlock = new PatternBlock(pattern);
        ItemStack item = patternBlock.toItem();
        if (item == null) {
            throw new IOException("Error reading pattern block");
        }
        return item;
    }

    private ItemStack readMask(MinecraftInputStream stream) throws IOException {
        byte type = stream.readByte();
        if (type == 0) {
            return readRegularItem(stream);
        }

        String materialName = stream.readUTF();
        Material material = Material.fromKey(materialName);
        material = material == null ? Material.LEATHER_CHESTPLATE : material;

        Color armorColor;
        if (stream.readBoolean()) {
            armorColor = new Color(stream.readUnsignedByte(), stream.readUnsignedByte(), stream.readUnsignedByte());
        } else {
            armorColor = new Color(255, 0, 0);
        }

        MasksUnion union = MasksUnion.BY_DB_KEY.get(stream.readUTF(), MaskItem.DEFAULT_UNION);

        int masksCount = stream.readUnsignedByte();
        List<MaskWithData<?>> masks = new ArrayList<>(masksCount);
        for (int i = 0; i < masksCount; ++i) {
            MaskWithData mask = stream.read(MaskWithData.MC_CODEC);
            masks.add(mask);
        }

        MaskItem maskItem = new MaskItem(material, armorColor, union, masks);
        return maskItem.renderItem();
    }
}
