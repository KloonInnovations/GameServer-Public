package io.kloon.gameserver;

import io.kloon.gameserver.minestom.components.ComponentWrapper;
import io.kloon.gameserver.modes.creative.CreativeMode;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;

import java.util.List;

public final class MiniMessageTemplate {
    public static final TextColor TITLE_COLOR = TextColor.color(255, 81, 142);
    public static final TextColor INFRA_COLOR = TextColor.color(234, 121, 159);

    public static final MiniMessage miniMessage = MiniMessage.builder()
            .editTags(builder -> {
                builder.tag("!cta", Tag.preProcessParsed("<red>✖ "));
                builder.tag("cta", Tag.preProcessParsed(STR."<#FFE056>\{InputFmt.CLICK} "));
                builder.tag("lcta", Tag.preProcessParsed(STR."<#FFE056>\{InputFmt.LEFT_CLICK} "));
                builder.tag("rcta", Tag.preProcessParsed(STR."<aqua>\{InputFmt.RIGHT_CLICK} "));
                builder.tag("title", Tag.preProcessParsed(STR."<\{TITLE_COLOR.asHexString()}>"));
                builder.tag("cmd", Tag.preProcessParsed("<dark_gray>/")); // 🖮
                builder.tag("undo", Tag.preProcessParsed("<dark_gray>⏪"));
                builder.tag("can_undo", Tag.preProcessParsed("<dark_gray>⏪ Can /undo"));
                builder.tag("snipe_target", Tag.preProcessParsed(STR."<#D2FF63>\{CreativeMode.SNIPE_ICON}snipe target"));
                builder.tag("selection", Tag.preProcessParsed(STR."<#A8FFE7>\{CreativeMode.SELECTION_ICON}selection"));
            })
            .build();

    private MiniMessageTemplate() {}

    public static final StringTemplate.Processor<Component, RuntimeException> MM = stringTemplate -> {
        String interpolated = STR.process(stringTemplate);
        return toComponent(interpolated);
    };

    public static Component toComponent(String string) {
        return miniMessage.deserialize("<!i>" + string); // !i added to alleviate the default italic in menus
    }

    public static final int WRAP_LENGTH = 32;

    public static final StringTemplate.Processor<List<Component>, RuntimeException> MM_WRAP = stringTemplate -> {
        Component component = MM.process(stringTemplate);
        return ComponentWrapper.wrap(component, WRAP_LENGTH);
    };
}
