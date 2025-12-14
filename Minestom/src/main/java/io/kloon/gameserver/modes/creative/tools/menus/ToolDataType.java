package io.kloon.gameserver.modes.creative.tools.menus;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.color.Color;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public enum ToolDataType {
    ITEM_BOUND(new Color(210, 140, 140), "SETTING", "Tool Setting"),
    PLAYER_BOUND(new Color(140, 210, 140), "PREF", "Player Preference"),
    MASK_BOUND(new Color(207, 135, 255), "MASK", "Mask Setting"),
    PATTERN_BOUND(new Color(255, 135, 213), "PATTERN", "Pattern Setting"),
    ;

    private final Color color;
    private final TextColor textColor;
    private final String subject;
    private final String subtitle;

    ToolDataType(Color color, String subject, String subtitle) {
        this.color = color;
        this.textColor = TextColor.color(color);
        this.subject = subject;
        this.subtitle = subtitle;
    }

    public Color getColor() {
        return color;
    }

    public TextColor getTextColor() {
        return textColor;
    }

    public String getColorHex() {
        return textColor.asHexString();
    }

    public String getSubject() {
        return subject;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Component getLoreSubtitle() {
        return MM."<dark_gray>\{subtitle}";
    }

    public List<Component> lore() {
        List<Component> lore = new ArrayList<>();
        lore.add(getLoreSubtitle());
        lore.add(Component.empty());
        return lore;
    }

    public void sendPit(CreativePlayer player, Component details) {
        player.sendPit(color, subject, details);
    }

    public SentMessage sendMsg(CreativePlayer player, Component details) {
        return player.msg().send(MsgCat.TOOL, textColor, subject, details);
    }

    public SentMessage sendMsg(CreativePlayer player, Component details, SoundEvent sound, Pitch pitch) {
        return player.msg().send(MsgCat.TOOL, textColor, subject, details, sound, pitch);
    }

    public SentMessage sendMsg(CreativePlayer player, Component details, SoundEvent sound, double pitch) {
        return player.msg().send(MsgCat.TOOL, textColor, subject, details, sound, pitch);
    }

    public SentMessage sendMsg(CreativePlayer player, Component details, SoundEvent sound, double pitch, double volume) {
        return player.msg().send(MsgCat.TOOL, textColor, subject, details, sound, pitch, volume);
    }
}
