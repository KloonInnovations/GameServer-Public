package io.kloon.gameserver.modes.creative.tools.impl.teleport.menu.players;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.PointFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.TeleportChange;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.sound.SoundEvent;

import java.util.UUID;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.tools.impl.teleport.commands.TeleportCommand.*;

public class TeleportToPlayerButton implements ChestButton {
    private final UUID playerId;

    public TeleportToPlayerButton(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        Player targetPlayer = player.getInstance().getPlayerByUuid(playerId);
        if (!(targetPlayer instanceof CreativePlayer target)) {
            player.sendPit(NamedTextColor.RED, "NOT FOUND", MM."<gray>Couldn't find target player in instance!");
            return;
        }

        Pos posBefore = player.getPosition();

        Pos tpPos = target.getPosition();
        player.teleport(tpPos);

        SentMessage sentMsg = player.msg().send(MsgCat.TOOL,
                NamedTextColor.DARK_PURPLE, "TELEPORTED!", MM."<gray>to <green>\{target.getDisplayMM()}<gray> at <green>\{PointFmt.fmt10k(tpPos)}<gray>!",
                SoundEvent.ENTITY_ENDERMAN_TELEPORT, Pitch.base(1.3).addRand(0.25));

        player.addToHistory(CreativeToolType.TELEPORTER, "<dark_purple>Teleport menu!",
                sentMsg, new TeleportChange(posBefore, player.isFlying(), player));

        if (MSG_CD.get(new SenderAndRecipient(player.getUuid(), target.getUuid())).cooldownIfPossible()) {
            target.sendPit(NamedTextColor.DARK_PURPLE, "TP!", MM."\{player.getDisplayMM()} <gray>has <green>/teleport<gray>ed to you!");
        }

        player.closeInventory();
    }

    @Override
    public ItemStack renderButton(Player player) {
        Player targetPlayer = player.getInstance().getPlayerByUuid(playerId);
        if (!(targetPlayer instanceof CreativePlayer target)) {
            return ItemStack.AIR;
        }

        Component name = MM."\{target.getDisplayMM()}";

        Lore lore = new Lore();
        lore.add(MM."<dark_gray>Player in world");
        lore.addEmpty();

        Pos targetPos = target.getPosition();
        int distance = (int) Math.round(targetPos.distance(player.getPosition()));

        lore.add(MM."<gray>Position: <green>\{PointFmt.fmt10k(targetPos)}");
        if (distance == 1) {
            lore.add(MM."<gray>Distance: <aqua>\{NumberFmt.NO_DECIMAL.format(distance)} block away!");
        } else {
            lore.add(MM."<gray>Distance: <aqua>\{NumberFmt.NO_DECIMAL.format(distance)} blocks away!");
        }
        lore.addEmpty();

        lore.add("<cta>Click to teleport!");

        HeadProfile targetHead = SkinCache.get(targetPlayer);

        return MenuStack.ofHead(targetHead).name(name).lore(lore).build();
    }
}
