package io.kloon.gameserver.modes.creative.menu.patterns;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.PassthroughPattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SelectAirButton implements ChestButton {
    private final CreativeConsumer<CreativePattern> onSelect;

    private boolean offerPassthrough = false;

    public SelectAirButton(CreativeConsumer<CreativePattern> onSelect) {
        this.onSelect = onSelect;
    }

    public static SelectAirButton selectBlock(CreativeConsumer<Block> onSelect) {
        return new SelectAirButton((player, pattern) -> {
            if (pattern instanceof SingleBlockPattern single) {
                onSelect.accept(player, single.getBlock());
            }
        });
    }

    public SelectAirButton offerPassthrough() {
        this.offerPassthrough = true;
        return this;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        if (offerPassthrough && click.isRightClick()) {
            onSelect.accept(player, new PassthroughPattern());
        } else {
            onSelect.accept(player, new SingleBlockPattern(Block.AIR));
        }
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Air";

        Lore lore = new Lore();
        lore.add(MM."<dark_gray>ID \{Block.AIR.key().value()}");
        lore.addEmpty();
        lore.wrap(MM."<gray>Let your creativity FLOW with this incredibly tangible block.");
        lore.addEmpty();

        if (offerPassthrough) {
            lore.add("<rcta>Click for passthrough!");
            lore.add("<lcta>Click to pick air!");
        } else {
            lore.add("<cta>Click to pick air!");
        }

        return MenuStack.of(Material.MILK_BUCKET, name, lore);
    }
}
