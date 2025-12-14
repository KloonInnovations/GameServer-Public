package io.kloon.gameserver.chestmenus.listing.cycle;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CycleButton<T extends CycleLabelable> implements ChestButton {
    private static final Logger LOG = LoggerFactory.getLogger(CycleButton.class);

    protected final Cycle<T> cycle;
    private final Integer slot;

    private Component title = MM."<title>Make a choice";
    private List<Component> description = new ArrayList<>();
    private Supplier<ItemBuilder2> icon = () -> MenuStack.of(Material.HOPPER);
    private BiConsumer<Player, T> onClick;

    private static final PlayerTickCooldownMap CLICK_COOLDOWN = new PlayerTickCooldownMap(2);

    public CycleButton(Cycle<T> cycle) {
        this.cycle = cycle;
        this.slot = null;
    }

    public CycleButton(Cycle<T> cycle, int slot) {
        this.cycle = cycle;
        this.slot = slot;
    }

    public CycleButton<T> withTitle(Component title) {
        this.title = title;
        return this;
    }

    public CycleButton<T> withDescription(List<Component> description) {
        this.description = description;
        return this;
    }

    public CycleButton<T> withDescription(Lore lore) {
        this.description = lore.asList();
        return this;
    }

    public CycleButton<T> withIcon(Material icon) {
        this.icon = () -> MenuStack.of(icon);
        return this;
    }

    public CycleButton<T> withIcon(Supplier<ItemBuilder2> icon) {
        this.icon = icon;
        return this;
    }

    public CycleButton<T> withOnClick(BiConsumer<Player, T> onClick) {
        this.onClick = onClick;
        return this;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        if (!CLICK_COOLDOWN.get(player).cooldownIfPossible()) {
            return;
        }

        T before = cycle.getSelected();
        T after;
        if (click.isRightClick()) {
            after = cycle.goBackwards();
        } else {
            after = cycle.goForward();
        }
        if (before != after) {
            if (slot == null) {
                ChestMenuInv.rerender(player);
            } else {
                ChestMenuInv.rerenderButton(slot, player);
            }
        }
        try {
            if (onClick == null) {
                player.sendMessage(MM."<red>Something should probably happen now!");
            } else {
                onClick.accept(player, after);
            }
        } catch (Throwable t) {
            LOG.error("Error in onClick callback of cycle button", t);
            player.closeInventory();
            player.sendMessage(MM."<red>Error after clicking this button! Sorry!");
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        List<Component> lore = new ArrayList<>(description);
        lore.add(Component.empty());

        T selected = cycle.getSelected();
        cycle.asList().forEach(option -> {
            String subLabel = option.subLabel();
            if (subLabel == null) {
                if (selected == option) {
                    lore.add(MM."<yellow>✦ <title>\{option.label()}");
                } else {
                    lore.add(MM."<gray>✦ \{option.label()}");
                }
            } else {
                if (selected == option) {
                    lore.add(MM."<yellow>✦ <title>\{option.label()} <dark_gray>- \{subLabel}");
                } else {
                    lore.add(MM."<gray>✦ \{option.label()} <dark_gray>- \{subLabel}");
                }
            }
        });
        lore.add(Component.empty());

        if (selected == cycle.getFirst()) {
            lore.add(MM."<cta>Click to cycle!");
        } else {
            lore.add(MM."<rcta>Click to cycle back!");
            lore.add(MM."<lcta>Click to cycle!");
        }

        return icon.get().name(title).lore(lore).build();
    }
}
