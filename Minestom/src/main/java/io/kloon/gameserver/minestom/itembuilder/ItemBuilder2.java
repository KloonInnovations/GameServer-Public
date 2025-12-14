package io.kloon.gameserver.minestom.itembuilder;

import io.kloon.gameserver.chestmenus.builtin.StaticButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.utils.MaterialUtils;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponentMap;
import net.minestom.server.component.DataComponents;
import net.minestom.server.instance.block.jukebox.JukeboxSong;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.AttributeList;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.item.component.TooltipDisplay;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Unit;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class ItemBuilder2 {
    private final Material material;
    private final DataComponentMap.Builder components = DataComponentMap.builder();
    private int amount = 1;

    public ItemBuilder2(Material material) {
        this.material = material;
    }

    public ItemBuilder2 amount(int amount) {
        this.amount = amount;
        return this;
    }

    public <T> ItemBuilder2 set(DataComponent<T> component, T value) {
        components.set(component, value);
        return this;
    }

    public <T> ItemBuilder2 tag(Tag<T> tag, T value) {
        CustomData customData = components.get(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .withTag(tag, value);
        components.set(DataComponents.CUSTOM_DATA, customData);
        return this;
    }

    public ItemBuilder2 name(Component name) {
        components.set(DataComponents.CUSTOM_NAME, name);
        return this;
    }

    public ItemBuilder2 lore(Component lore) {
        return lore(Collections.singletonList(lore));
    }

    public ItemBuilder2 lore(List<Component> lore) {
        components.set(DataComponents.LORE, lore);
        return this;
    }

    public ItemBuilder2 lore(Lore lore) {
        return lore(lore.asList());
    }

    private static final Set<DataComponent<?>> HIDDEN_TOOLTIP_COMPONENTS = Set.of(
            DataComponents.BANNER_PATTERNS, DataComponents.BEES, DataComponents.BLOCK_ENTITY_DATA,
            DataComponents.BLOCK_STATE, DataComponents.BUNDLE_CONTENTS, DataComponents.CHARGED_PROJECTILES,
            DataComponents.CONTAINER, DataComponents.CONTAINER_LOOT, DataComponents.FIREWORK_EXPLOSION,
            DataComponents.FIREWORKS, DataComponents.INSTRUMENT, DataComponents.MAP_ID,
            DataComponents.PAINTING_VARIANT, DataComponents.POT_DECORATIONS, DataComponents.POTION_CONTENTS,
            DataComponents.TROPICAL_FISH_PATTERN, DataComponents.WRITTEN_BOOK_CONTENT,
            DataComponents.JUKEBOX_PLAYABLE, DataComponents.TRIM, DataComponents.DYED_COLOR,
            DataComponents.UNBREAKABLE, DataComponents.ATTRIBUTE_MODIFIERS
    );

    public ItemBuilder2 hideFlags() {
        components.set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(false, HIDDEN_TOOLTIP_COMPONENTS));
        return this;
    }

    public ItemBuilder2 glowing(boolean glowing) {
        components.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, glowing);
        return this;
    }

    public ItemBuilder2 glowing() {
        components.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return this;
    }

    public ItemStack build() {
        DataComponentMap components = this.components.build();
        return ItemStack.of(material, amount, components);
    }

    public StaticButton buildButton() {
        return new StaticButton(build());
    }
}
