package io.kloon.gameserver.chestmenus.states;

import io.kloon.gameserver.MiniMessageTemplate;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;

import java.util.function.Function;

public class ButtonState {
    private final Function<Player, String> callToAction;

    private Function<Player, String> chatMessage;
    private Material icon;

    private boolean positive = false;

    public ButtonState(String callToActionMM) {
        this(player -> callToActionMM);
    }

    public ButtonState(Function<Player, String> callToActionMM) {
        this.callToAction = callToActionMM;
    }

    public ButtonState withChat(String chatMessageMM) {
        return withChat(player -> chatMessageMM);
    }

    public ButtonState withChat(Function<Player, String> chatMessageMM) {
        this.chatMessage = chatMessageMM;
        return this;
    }

    public ButtonState withIcon(Material icon) {
        this.icon = icon;
        return this;
    }

    public ButtonState withPositive() {
        this.positive = true;
        return this;
    }

    public Component getCallToAction(Player player) {
        String string = callToAction.apply(player);
        return MiniMessageTemplate.toComponent(string);
    }

    public Component getChatMessage(Player player) {
        if (chatMessage == null) {
            return getCallToAction(player);
        }

        String string = chatMessage.apply(player);
        return MiniMessageTemplate.toComponent(string);
    }

    public void sendChatMessage(Player player) {
        Component message = getChatMessage(player);
        player.sendMessage(message);
    }

    public Material getIcon(Material def) {
        return icon == null ? def : icon;
    }

    public boolean isPositive() {
        return positive;
    }
}
