package io.kloon.gameserver.modes.creative.menu.worldadmin.size;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.states.ButtonState;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.WorldBorderCommand;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldSize;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.infra.ranks.StoreRank;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WorldSizeButton implements ChestButton {
    private final ChestMenu parent;
    private final CreativeWorldSize size;

    public WorldSizeButton(ChestMenu parent, CreativeWorldSize size) {
        this.parent = parent;
        this.size = size;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        ButtonState state = getState(player);
        if (state != CAN_SELECT) {
            state.sendChatMessage(player);
            return;
        }

        CreativeWorldStorage storage = player.getInstance().getWorldStorage();
        storage.setWorldSize(size);

        WorldBorderCommand.applyWorldBorder(player);

        player.broadcast().send(MsgCat.WORLD,
                NamedTextColor.GREEN, "AYAYA!", MM."<gray>Set world size to <green>\{size.getCuteName()}<gray>!",
                SoundEvent.ENTITY_EVOKER_PREPARE_SUMMON, 0.7 + size.ordinal() * 0.1);

        parent.display(player);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<title>\{size.getCuteName()}";

        Lore lore = new Lore();

        int diameter = size.getChunksDiameter();
        lore.add(MM."<gray>Size: <light_purple>\{diameter}x\{diameter} chunks");

        int blocksDiameter = diameter * 16;
        int blocks = blocksDiameter * blocksDiameter;
        lore.add(MM."<gray>aka <green>\{NumberFmt.NO_DECIMAL.format(blocks)} blocks");
        lore.addEmpty();

        ButtonState state = getState(player);
        lore.add(state.getCallToAction(player));
        if (state == REQUIRES_STORE_RANK) {
            lore.add(MM."<gold>Visit https://kloon.io/store");
        }

        ItemBuilder2 builder = MenuStack.of(size.getIcon()).name(name).lore(lore);
        if (state == IS_SELECTED) {
            builder.glowing();
        }
        return builder.build();
    }

    public ButtonState getState(CreativePlayer player) {
        if (!player.getRanks().hasStoreRankOrNewer(StoreRank.EARLY_ADOPTER) && size != CreativeWorldSize.SEVEN_X_SEVEN) {
            return REQUIRES_STORE_RANK;
        }

        CreativeInstance instance = player.getInstance();
        if (!instance.getWorldDef().ownership().isOwner(player)) {
            return NOT_WORLD_OWNER;
        }

        CreativeWorldSize currentSize = instance.getWorldStorage().getWorldSize();
        if (currentSize == size) {
            return IS_SELECTED;
        }

        if (size.ordinal() < currentSize.ordinal()) {
            return CANNOT_SHRINK;
        }

        return CAN_SELECT;
    }

    private static final ButtonState CAN_SELECT = new ButtonState("<cta>Click to set!");
    private static final ButtonState NOT_WORLD_OWNER = new ButtonState("<!cta>Not world owner!");
    private static final ButtonState IS_SELECTED = new ButtonState("<green>The world is this big!");
    private static final ButtonState CANNOT_SHRINK = new ButtonState("<!cta>Cannot shrink world!").withChat("<red>Awkwardly, you cannot shrink the world's size at this time!");
    private static final ButtonState REQUIRES_STORE_RANK = new ButtonState("<!cta>Requires store rank!");
}
