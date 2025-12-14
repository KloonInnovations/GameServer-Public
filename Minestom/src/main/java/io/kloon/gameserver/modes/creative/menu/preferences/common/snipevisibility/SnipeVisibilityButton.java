package io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility;

import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class SnipeVisibilityButton extends CycleButton<SnipeVisibility> {
    public SnipeVisibilityButton(int slot) {
        super(new Cycle<>(SnipeVisibility.values()), slot);

        withIcon(Material.SPYGLASS);
        withTitle(MM."<title>Preview Visibility");
        withDescription(MM_WRAP."<gray>Change how discreet or apparent the preview shape is.");
        withOnClick((p, vis) -> {
            CreativePlayer player = (CreativePlayer) p;
            player.getCreativeStorage().getSnipe().setShapeVisibility(vis);

            player.sendPit(NamedTextColor.LIGHT_PURPLE, "VISBILITY!", MM."<gray>Set to \{vis.label()}<gray>!");
            player.playSound(SoundEvent.ENTITY_ALLAY_ITEM_GIVEN, 1.1 + 0.2 * vis.ordinal());

            ChestMenuInv.rerenderButton(slot, player);
        });
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        SnipeVisibility snipeVisibility = player.getSnipe().getVisibility();
        cycle.select(snipeVisibility);

        return super.renderButton(p);
    }
}
