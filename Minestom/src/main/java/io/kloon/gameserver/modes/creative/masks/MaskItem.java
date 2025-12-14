package io.kloon.gameserver.modes.creative.masks;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.armor.ArmorSlot;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.nbt.NBT;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.storage.inventories.items.CreativeItemEncoderV1;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.security.ToolSignature;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.color.Color;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public final class MaskItem {
    public static final Tag<BinaryTag> TAG = Tag.NBT("kloon_creative:mask_fashion");

    public static final TextColor TEXT_COLOR = TextColor.color(195, 99, 255);
    public static final String TEXT_HEX = TEXT_COLOR.asHexString();

    private final Material material;
    private final Color armorColor;
    private final MasksUnion union;
    private final List<MaskWithData<?>> masks;

    public static final int MASKS_LIMIT = 5;
    public static final MasksUnion DEFAULT_UNION = MasksUnion.OR;

    public MaskItem(Material material, Color armorColor, MasksUnion union, List<MaskWithData<?>> masks) {
        this.material = material;
        this.armorColor = armorColor;
        this.union = union;
        this.masks = masks;
    }

    public Material getMaterial() {
        return material;
    }

    public Color getArmorColor() {
        return armorColor;
    }

    public MasksUnion getUnion() {
        return union;
    }

    public List<MaskWithData<?>> getMasks() {
        return masks;
    }

    public MaskItem withMaterial(Material material) {
        return new MaskItem(material, armorColor, union, new ArrayList<>(masks));
    }

    public MaskItem withArmorColor(Color color) {
        return new MaskItem(material, color, union, new ArrayList<>(masks));
    }

    public MaskItem withUnion(MasksUnion union) {
        return new MaskItem(material, armorColor, union, new ArrayList<>(masks));
    }

    public MaskItem withAddedMask(MaskWithData<?> added) {
        List<MaskWithData<?>> masksCopy = new ArrayList<>(masks);
        masksCopy.removeIf(mask -> mask.id().equals(added.id()));
        masksCopy.add(added);
        masksCopy = masksCopy.subList(0, Math.min(MASKS_LIMIT, masksCopy.size()));
        return new MaskItem(material, armorColor, union, masksCopy);
    }

    public MaskItem withEditedMask(MaskWithData<?> edited) {
        List<MaskWithData<?>> masksCopy = new ArrayList<>(masks);
        masksCopy.replaceAll(mask -> {
            if (mask.id().equals(edited.id())) {
                return edited;
            }
            return mask;
        });
        return new MaskItem(material, armorColor, union, masksCopy);
    }

    public MaskItem withRemovedMask(ObjectId maskId) {
        List<MaskWithData<?>> masksCopy = new ArrayList<>(masks);
        masksCopy.removeIf(mask -> mask.id().equals(maskId));
        return new MaskItem(material, armorColor, union, masksCopy);
    }

    public ItemStack renderItem() {
        ItemBuilder2 builder = new ItemBuilder2(material);
        builder.set(DataComponents.DYED_COLOR, armorColor);
        builder.name(getItemName());
        builder.lore(getItemLore());
        builder.hideFlags();

        CompoundBinaryTag fashionNbt = NBT.compound(c -> {
            c.putString("union", union.getDbKey());

            List<CompoundBinaryTag> maskTags = masks.stream().map(MaskWithData.NBT_CODEC::encode).toList();
            c.putCompoundList("masks", maskTags);
        });
        builder.tag(TAG, fashionNbt);

        ItemStack stack = builder.build();
        stack = ToolSignature.signed(stack);
        return stack;
    }

    private Component getItemName() {
        if (masks.isEmpty()) {
            return MM."<red>No Mask";
        }
        MaskWithData first = masks.getFirst();
        MaskType type = first.type();
        if (masks.size() == 1) {
            String appendMM = type.renderNameAppendMM(first);
            return appendMM == null
                    ? MM."<\{type.getColor().asHexString()}>\{type.getNameMM()}"
                    : MM."<\{type.getColor().asHexString()}>\{type.getNameMM()} <white>(\{appendMM})";
        }
        boolean allSameType = masks.stream().allMatch(m -> m.type() == type);
        if (allSameType) {
            return MM."<\{type.getColor().asHexString()}>Multi-\{type.getNameMM()}";
        } else {
            return MM."<\{TEXT_COLOR.asHexString()}>Multi-Mask";
        }
    }

    private Lore getItemLore() {
        Lore lore = new Lore();
        lore.add(MM."<dark_gray>Wearable Mask");
        lore.addEmpty();

        ArmorSlot slot = ArmorSlot.get(material);
        lore.add(MM."<green>\{slot.icon()} <\{TEXT_COLOR.asHexString()}><b>WEARABLE");
        lore.wrap("<gray>While worn, filters what blocks your tools may edit.");
        lore.addEmpty();

        if (masks.isEmpty()) {
            lore.wrap("<gray>Somehow, this mask item doesn't have any masks defined.");
        } else {
            if (masks.size() > 1) {
                switch (union) {
                    case OR -> lore.add("<green>At least one must be true");
                    case AND -> lore.add("<green>All must be true");
                }
            } else {
                lore.add("<green>Must be true");
            }
            masks.forEach(mask -> {
                MaskType type = mask.type();
                lore.add(type.getConditionBulletPoint(mask.data(), mask.negated(), masks.size() == 1));
            });
        }
        return lore;
    }

    public void openEditMenu(CreativePlayer player, ItemRef itemRef) {
        new EditMaskItemMenu(player, this, itemRef).display(player);
    }

    public static boolean is(ItemStack item) {
        return item.getTag(TAG) instanceof ListBinaryTag;
    }

    @Nullable
    public static MaskItem get(ItemStack item) {
        BinaryTag tagNbt = item.getTag(TAG);
        if (!(tagNbt instanceof CompoundBinaryTag compound)) {
            return null;
        }

        RGBLike dyedItemColor = item.get(DataComponents.DYED_COLOR);
        Color color = dyedItemColor == null
                ? new Color(255, 0, 0)
                : new Color(dyedItemColor);

        MasksUnion union = MasksUnion.BY_DB_KEY.get(compound.getString("union"), DEFAULT_UNION);

        ListBinaryTag listNbt = compound.getList("masks");
        List<MaskWithData<?>> masks = new ArrayList<>(listNbt.size());
        for (int i = 0; i < listNbt.size(); ++i) {
            CompoundBinaryTag maskNbt = listNbt.getCompound(i);
            MaskWithData<?> mask = MaskWithData.NBT_CODEC.decode(maskNbt);
            masks.add(mask);
        }
        return new MaskItem(item.material(), color, union, masks);
    }

    public static Color generateRandomArmorColor() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        HSVLike randHsv = HSVLike.hsvLike(
                rand.nextFloat(),
                0.92f + rand.nextFloat() * 0.08f,
                0.95f + rand.nextFloat() * 0.05f);
        return new Color(TextColor.color(randHsv));
    }

    public static byte[] encodeSigContents(ItemStack item) throws IOException {
        return MinecraftOutputStream.toBytes(item, CreativeItemEncoderV1.INSTANCE);
    }
}
