package io.kloon.gameserver.modes.creative.masks;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.armorpicker.PlayerArmorPicker;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskWorkCache;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.NoSettingsMaskMenu;
import io.kloon.gameserver.modes.creative.tools.data.ToolData;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class MaskType<Data> {
    private final String dbKey;
    private final Class<Data> itemBoundClass;
    private final Supplier<Data> defaultItemBound;

    public static final Gson DATA_GSON = ToolData.DATA_GSON;
    protected static final String PREFIX = "<dark_gray>●<gray>";

    public MaskType(String dbKey, Class<Data> itemBoundClass, Supplier<Data> defaultItemBound) {
        this.dbKey = dbKey;
        this.itemBoundClass = itemBoundClass;
        this.defaultItemBound = defaultItemBound;
    }

    public final String getDbKey() {
        return dbKey;
    }

    public String getCommandLabel() {
        return dbKey;
    }

    public Data getData(String dataJson) {
        if (Strings.isNullOrEmpty(dataJson)) {
            return defaultItemBound.get();
        }
        return DATA_GSON.fromJson(dataJson, itemBoundClass);
    }

    @Nullable
    public ItemStack giveToPlayer(CreativePlayer player) {
        MaskWithData<Data> data = createDefault();
        return giveToPlayer(player, data);
    }

    @Nullable
    public ItemStack giveToPlayer(CreativePlayer player, MaskWithData<Data> data) {
        ItemStack item = renderItem(player, data);
        boolean added = player.getInventoryExtras().grab(item);
        if (added) {
            player.msg().send(MsgCat.INVENTORY,
                    NamedTextColor.GREEN, "MASK!", MM."\{getNameMM()} <gray>mask added to inventory!",
                    SoundEvent.BLOCK_AMETHYST_CLUSTER_STEP, Pitch.rng(0.5, 0.2));
            return item;
        } else {
            player.msg().send(MsgCat.INVENTORY,
                    NamedTextColor.RED, "OOPS!", MM."<gray>Couldn't add \{getNameMM()} <gray>to your inventory!",
                    SoundEvent.ENTITY_VILLAGER_NO, 1.0);
            return null;
        }
    }

    public final MaskWithData<Data> createDefault() {
        Data defaultData = defaultItemBound.get();
        return new MaskWithData<>(new ObjectId(), this, false, defaultData);
    }

    public ItemStack renderItem(CreativePlayer player, MaskWithData<Data> data) {
        Material material = new PlayerArmorPicker(player).pick();
        Color armorColor = MaskItem.generateRandomArmorColor();
        MaskItem maskItem = new MaskItem(material, armorColor, MaskItem.DEFAULT_UNION, Collections.singletonList(data));

        return maskItem.renderItem();
    }

    public abstract ItemBuilder2 getIcon();

    public abstract String getName();

    public String getNameMM() {
        return STR."<\{getColor().asHexString()}>\{getName()}";
    }

    @Nullable
    public String renderNameAppendMM(MaskWithData<Data> mask) {
        return null;
    }

    public TextColor getColor() {
        return MaskItem.TEXT_COLOR;
    }

    public abstract Lore getDatalessDescription();

    public abstract Lore getLore(Data data, boolean negated);

    public abstract Lore getConditionBulletPoint(Data data, boolean negated, boolean onlyPoint);

    public List<Command> createCommands() {
        return Collections.emptyList();
    }

    public ChestMenu createMaskMenu(EditMaskItemMenu parent, MaskWithData<Data> mask) {
        return new NoSettingsMaskMenu(parent, mask);
    }

    public abstract boolean matches(MaskWorkCache workCache, Data data, Block.Getter instance, Point blockPos, Block block);

    public boolean skip(MaskWorkCache workCache, Data data, Block block) {
        return false;
    }
}
