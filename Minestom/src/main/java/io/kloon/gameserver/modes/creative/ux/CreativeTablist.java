package io.kloon.gameserver.modes.creative.ux;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.buildpermits.menu.BuildPermitsMenu;
import io.kloon.gameserver.tablist.DefaultTablist;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreativeTablist extends DefaultTablist {
    public static final String STAIN = "\uD83C\uDF0C"; // 🌌
    public static final String STAR = "\uD83C\uDF20"; // 🌠
    public static final String CONSTRUCTION = "\uD83C\uDFD7"; // 🏗

    private final CreativePlayer viewer;

    public CreativeTablist(CreativePlayer viewer) {
        super(viewer);
        this.viewer = viewer;
    }

    @Override
    protected PlayerInfoUpdatePacket.Entry makeOnlineEntry(Player t) {
        CreativeInstance instance = viewer.getInstance();
        boolean sameInstance = instance == t.getInstance();
        if (!sameInstance || !(t instanceof CreativePlayer target)) {
            return super.makeOnlineEntry(t);
        }

        String prefix = getPrefixMM(instance, target);
        Component displayName = MM."\{prefix} \{target.getColoredMM()}";
        return createPlayerEntry(t, displayName);
    }

    private String getPrefixMM(CreativeInstance instance, CreativePlayer target) {
        if (instance.getWorldDef().ownership().isOwner(target)) {
            return STR."<gold>\{CONSTRUCTION}";
        } else if (instance.getPermitForPlayer(target) != null) {
            return STR."<gold>\{BuildPermitsMenu.ICON}";
        }
        return STR."<#FF266E>\{EARTH_AMERICA}";
    }
}
