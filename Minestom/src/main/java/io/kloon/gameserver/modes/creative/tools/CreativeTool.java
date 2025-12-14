package io.kloon.gameserver.modes.creative.tools;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.impl.InventoryToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ItemBoundPattern;
import io.kloon.gameserver.modes.creative.tools.data.ToolData;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.security.ToolSignature;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public abstract class CreativeTool<TItemBound, TPlayerBound> {
    private static final Logger LOG = LoggerFactory.getLogger(CreativeTool.class);

    public static final Tag<String> TOOL_TYPE_TAG = Tag.String("kloon_creative:tool_type");
    public static final Tag<String> TOOL_DATA = Tag.String("kloon_creative:tool_data");
    public static final Tag<String> UNSTACKABLE = Tag.String("kloon_creative:unstackable");

    protected final CreativeToolType toolType;
    private final ToolDataDef<TItemBound, TPlayerBound> dataDef;

    public CreativeTool(CreativeToolType type, ToolDataDef<TItemBound, TPlayerBound> dataDef) {
        this.toolType = type;
        this.dataDef = dataDef;
    }

    public CreativeToolType getType() {
        return toolType;
    }

    public final void giveToPlayer(CreativePlayer player) {
        ItemStack item = renderNewItem(player);
        giveToPlayer(player, item);
    }

    public final void giveToPlayer(CreativePlayer player, TItemBound settings) {
        ItemStack item = renderItem(settings, getPlayerBound(player));
        giveToPlayer(player, item);
    }

    private void giveToPlayer(CreativePlayer player, ItemStack item) {
        boolean added = player.getInventory().addItemStack(item);
        if (added) {
            player.msg().send(MsgCat.INVENTORY,
                    NamedTextColor.GREEN, "ADDED!", MM."<gray>\{toolType.getDisplayName()} added to inventory!",
                    SoundEvent.BLOCK_AMETHYST_CLUSTER_STEP, 1.0);
        } else {
            player.msg().send(MsgCat.INVENTORY,
                    NamedTextColor.RED, "OOPS!", MM."<gray>Couldn't add \{toolType.getDisplayName()} to your inventory!",
                    SoundEvent.ENTITY_VILLAGER_NO, 1.0);
        }
    }

    public boolean isTool(ItemStack item) {
        String toolDbKey = item.getTag(TOOL_TYPE_TAG);
        return toolType.getDbKey().equals(toolDbKey);
    }

    public final ItemStack renderNewItem(CreativePlayer player) {
        TPlayerBound playerBound = player.getToolsStorage().get(toolType, dataDef.playerBoundClass(), dataDef.defaultPlayerBound());
        return renderItem(dataDef.createDefaultItemBound(), playerBound);
    }

    public final ItemStack renderItem(TItemBound itemBound, CreativePlayer player) {
        TPlayerBound playerBound = getPlayerBound(player);
        return renderItem(itemBound, playerBound);
    }

    public final ItemStack renderItem(TItemBound itemBound, TPlayerBound playerBound) {
        try {
            ItemStack itemStack = renderOverride(itemBound, playerBound);
            if (itemStack == null) {
                itemStack = toolBuilder(itemBound).lore(getItemLore(itemBound, playerBound)).build();
            }
            itemStack = ToolSignature.signed(itemStack);
            return itemStack;
        } catch (Throwable t) {
            LOG.error(STR."Error rendering tool \{toolType}", t);
            return new ItemBuilder2(Material.STONE)
                    .tag(UNSTACKABLE, new ObjectId().toHexString())
                    .name(MM."<red>Error!")
                    .lore(MM."<red>Error generating tool!")
                    .build();
        }
    }

    @Nullable
    public ItemStack renderOverride(TItemBound itemBound, TPlayerBound playerBound) {
        return null;
    }

    protected List<Component> getItemLore(TItemBound settings, TPlayerBound preferences) {
        List<Component> lore = new ArrayList<>();
        lore.add(toolType.getCategory().getLoreLine());
        lore.add(Component.empty());
        try {
            writeUsage(lore, settings, preferences);
        } catch (Throwable t) {
            LOG.error("Error rendering tool usage for " + toolType, t);
            lore.add(Component.empty());
            lore.addAll(MM_WRAP."<red>\uD83D\uDEAB Error rendering tool description."); // 🚫
        }
        return lore;
    }

    public Component renderName(TItemBound settings) {
        if (settings instanceof ItemBoundPattern settingsWithPattern) {
            if (settingsWithPattern.hasPattern()) {
                CreativePattern pattern = settingsWithPattern.getPattern();
                if (pattern != null) {
                    return MM."<white>\{toolType.getDisplayName()} (\{pattern.labelMM()}<white>)";
                }
            }
        }
        return MM."<white>\{toolType.getDisplayName()}";
    }

    public void writeUsage(List<Component> lore, TItemBound settings, TPlayerBound preferences) {
        lore.addAll(MM_WRAP."<gray>This tool doesn't have usage instructions.");
    }

    protected final ItemBuilder2 toolBuilder(TItemBound settings) {
        return toolBuilder(settings, toolType.getMaterial());
    }

    protected final ItemBuilder2 toolBuilder(TItemBound settings, Material forceMaterial) {
        ItemBuilder2 builder = new ItemBuilder2(forceMaterial)
                .name(renderName(settings))
                .tag(TOOL_TYPE_TAG, toolType.getDbKey())
                .tag(UNSTACKABLE, new ObjectId().toHexString())
                .hideFlags();

        if (settings != null) {
            try {
                String itemBoundJson = ToolData.DATA_GSON.toJson(settings);
                builder.tag(TOOL_DATA, itemBoundJson);
            } catch (Throwable t) {
                LOG.error(STR."Error converting \{settings} to json", t);
            }
        }

        return builder;
    }

    public TItemBound getItemBound(ToolClick click) {
        return getItemBound(click.getItem());
    }

    public TItemBound getItemBound(ItemRef itemRef) {
        return getItemBound(itemRef.getItem());
    }

    public TItemBound getItemBound(ItemStack item) {
        String dataJson = item.getTag(TOOL_DATA);
        return getItemBound(dataJson);
    }

    public TItemBound getItemBound(String itemDataJson) {
        if (itemDataJson == null) {
            return dataDef.createDefaultItemBound();
        } if (dataDef.itemBoundClass() == Void.class) {
            return dataDef.createDefaultItemBound();
        }
        return ToolData.DATA_GSON.fromJson(itemDataJson, dataDef.itemBoundClass());
    }

    public boolean editItemBound(CreativePlayer player, ItemRef itemRef, Consumer<TItemBound> editor) {
        TPlayerBound playerBound = getPlayerBound(player);
        TItemBound itemBound = getItemBound(itemRef.getItem());

        editor.accept(itemBound);

        ItemStack editedItem = renderItem(itemBound, playerBound);

        boolean edited = itemRef.setIfDidntChange(editedItem);
        if (edited) {
            return true;
        }

        player.sendPit(NamedTextColor.RED, "OOPS", MM."<gray>Couldn't edit your tool because it somehow changed preemptively!");
        player.closeInventory();
        return false;
    }

    public TPlayerBound getPlayerBound(CreativePlayer player) {
        return player.getToolsStorage().get(toolType, dataDef.playerBoundClass(), dataDef.defaultPlayerBound());
    }

    public void editPlayerBound(CreativePlayer player, Consumer<TPlayerBound> editor) {
        TPlayerBound playerBound = getPlayerBound(player);
        editor.accept(playerBound);
        player.getToolsStorage().set(toolType, playerBound);
    }

    public TItemBound createDefaultItemBound() {
        return dataDef.createDefaultItemBound();
    }

    public TPlayerBound createDefaultPlayerBound() {
        return dataDef.createDefaultPlayerBound();
    }

    @Nullable
    public ToolSnipe<TItemBound> createSnipe(CreativePlayer player) {
        return null;
    }

    @Nullable
    public ToolSidebar<TItemBound, TPlayerBound> createSidebar() {
        return null;
    }

    @Nullable
    public final Lore generateSidebar(CreativePlayer player, ItemStack inHand) {
        ToolSidebar<TItemBound, TPlayerBound> sidebar = createSidebar();
        if (sidebar == null) {
            return null;
        }

        TItemBound itemBound = getItemBound(inHand);
        TPlayerBound playerBound = getPlayerBound(player);
        return sidebar.generate(player, itemBound, playerBound);
    }

    public boolean usesQSnipe(CreativePlayer player) {
        return createSnipe(player) != null;
    }

    public List<Command> createCommands() {
        return Collections.emptyList();
    }

    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        player.playSound(SoundEvent.BLOCK_NETHER_WOOD_FALL, 0.7);
        player.sendPit(NamedTextColor.DARK_AQUA, "ZOOP!", MM."<gray>\{toolType.getDisplayName()} doesn't have any settings!");
    }

    public final void handleClick(CreativePlayer player, ToolClick click) {
        boolean hasEditPerm = player.canEditWorld();
        if (!hasEditPerm && !canUseWithoutEditPerm(player, click)) {
            player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_BIT, Pitch.base(0.5).addRand(0.45));
            player.sendPit(NamedTextColor.RED, "OOPS!", MM."<gray>Can't use this without permission to edit the world!");
            return;
        }

        handleUse(player, click);
    }

    protected boolean canUseWithoutEditPerm(CreativePlayer player, ToolClick click) {
        return false;
    }

    protected abstract void handleUse(CreativePlayer player, ToolClick click);

    public void handleClickInInventory(CreativePlayer player, InventoryToolClick click) {

    }
}
