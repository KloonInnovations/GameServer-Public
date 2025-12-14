package io.kloon.gameserver.modes.creative.storage.inventories.items;

import io.kloon.gameserver.minestom.io.MinecraftEncoder;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.MasksUnion;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.blocks.PatternBlock;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.component.DataComponents;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.io.DataOutputStream;
import java.io.IOException;

import static io.kloon.gameserver.modes.creative.storage.inventories.CreativeInventoryCodecV1.*;

public class CreativeItemEncoderV1 implements MinecraftEncoder<ItemStack> {
    public static final CreativeItemEncoderV1 INSTANCE = new CreativeItemEncoderV1();

    @Override
    public void encode(ItemStack item, MinecraftOutputStream out) throws IOException {
        if (item == null || item.material() == Material.AIR) {
            out.writeByte(ITEM_TYPE_AIR);
        } else if (item.hasTag(CreativeTool.TOOL_TYPE_TAG)) {
            out.writeByte(ITEM_TYPE_TOOL);
            encodeTool(item, out);
        } else if (item.hasTag(TinkeredBlock.TAG)) {
            out.writeByte(ITEM_TYPE_TINKERED_BLOCK);
            encodeTinkeredBlock(item, out);
        } else if (item.hasTag(PatternBlock.TAG)) {
            out.writeByte(ITEM_TYPE_PATTERN_BLOCK);
            encodePatternBlock(item, out);
        } else if (item.hasTag(MaskItem.TAG)) {
            out.writeByte(ITEM_TYPE_MASK);
            encodeMask(item, out);
        } else {
            out.writeByte(ITEM_TYPE_NBT);
            encodeRegularItem(item, out);
        }
    }

    private void encodeTool(ItemStack item, DataOutputStream stream) throws IOException {
        String toolType = item.getTag(CreativeTool.TOOL_TYPE_TAG);
        stream.writeUTF(toolType);

        String toolData = item.getTag(CreativeTool.TOOL_DATA);
        if (toolData == null) {
            stream.writeBoolean(false);
        } else {
            stream.writeBoolean(true);
            stream.writeUTF(toolData);
        }
    }

    private void encodeRegularItem(ItemStack item, DataOutputStream stream) throws IOException {
        CompoundBinaryTag itemNbt = item.toItemNBT();
        itemNbt.type().write(itemNbt, stream);
    }

    private void encodeTinkeredBlock(ItemStack item, MinecraftOutputStream stream) throws IOException {
        TinkeredBlock tinkered = TinkeredBlock.get(item);
        if (tinkered == null) {
            stream.writeByte(0);
            encodeRegularItem(item, stream);
            return;
        }

        stream.writeByte(2);

        Block block = tinkered.block();
        DumbPalette.BLOCK_CODEC_FULL.encode(block, stream);
    }

    private void encodePatternBlock(ItemStack item, MinecraftOutputStream stream) throws IOException {
        PatternBlock patternBlock = PatternBlock.get(item);
        if (patternBlock == null) {
            stream.writeByte(0);
            encodeRegularItem(item, stream);
            return;
        }

        stream.writeByte(1);

        CreativePattern pattern = patternBlock.pattern();
        stream.write(pattern, CreativePattern.CODEC);
    }

    private void encodeMask(ItemStack item, MinecraftOutputStream stream) throws IOException {
        BinaryTag tag = item.getTag(MaskItem.TAG);
        if (!(tag instanceof CompoundBinaryTag compound)) {
            stream.writeByte(0);
            encodeRegularItem(item, stream);
            return;
        }

        stream.writeByte(1);

        stream.writeUTF(item.material().name());

        RGBLike dyedColor = item.get(DataComponents.DYED_COLOR);
        if (dyedColor == null) {
            stream.writeBoolean(false);
        } else {
            stream.writeBoolean(true);
            stream.writeByte(dyedColor.red());
            stream.writeByte(dyedColor.green());
            stream.writeByte(dyedColor.blue());
        }

        MasksUnion union = MasksUnion.BY_DB_KEY.get("union", MaskItem.DEFAULT_UNION);
        stream.writeUTF(union.getDbKey());

        ListBinaryTag listNbt = compound.getList("masks");
        stream.writeByte(listNbt.size());
        for (int i = 0; i < listNbt.size(); ++i) {
            CompoundBinaryTag maskNbt = listNbt.getCompound(i);
            MaskWithData mask = MaskWithData.NBT_CODEC.decode(maskNbt);
            stream.write(mask, MaskWithData.MC_CODEC);
        }
    }
}
