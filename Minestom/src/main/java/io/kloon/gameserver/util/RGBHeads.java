package io.kloon.gameserver.util;

import com.google.gson.JsonObject;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.color.ColorUtils;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.color.Color;
import net.minestom.server.item.component.HeadProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RGBHeads {
    private static final Logger LOG = LoggerFactory.getLogger(RGBHeads.class);

    private RGBHeads() {}

    public static Map<Color, String> SKIN_VALUES_BY_COLOR = new HashMap<>();
    static {
        String resourceFile = "hexcolors.txt";
        try {
            InputStream stream = RGBHeads.class.getClassLoader().getResourceAsStream(resourceFile);
            List<String> lines = new String(stream.readAllBytes()).lines().toList();
            for (String line : lines) {
                String[] split = line.split(" ");
                int rgb = Integer.parseInt(split[0]);
                Color color = new Color(rgb);

                String skinUrlPart = split[1];
                String skinValue = skinValueFromUrlPart(skinUrlPart);

                SKIN_VALUES_BY_COLOR.put(color, skinValue);
            }
        } catch (Throwable t) {
            LOG.error(STR."Error loading \{resourceFile}", t);
        }
    }

    public static String getClosestSkinValue(RGBLike color) {
        RGBLike closest = ColorUtils.closestRGB(color, SKIN_VALUES_BY_COLOR.keySet());
        return SKIN_VALUES_BY_COLOR.get(new Color(closest));
    }

    public static ItemBuilder2 getClosestHead(RGBLike color) {
        String skinValue = getClosestSkinValue(color);
        HeadProfile head = SkinCache.toHead(skinValue);
        return MenuStack.ofHead(head);
    }

    private static String skinValueFromUrlPart(String urlPart) {
        JsonObject json = new JsonObject();

        JsonObject textures = new JsonObject();
        json.add("textures", textures);

        JsonObject SKIN = new JsonObject();
        textures.add("SKIN", SKIN);

        SKIN.addProperty("url", "https://textures.minecraft.net/texture/" + urlPart);

        return Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
    }
}
