package io.kloon.gameserver.minestom.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ComponentWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(ComponentWrapper.class);

    public static List<Component> wrap(Component component, int length) {
        if (!(component instanceof TextComponent text)) {
            return Collections.singletonList(component);
        }

        List<Component> wrapped = new ArrayList<>();

        List<TextComponent> parts = flatten(text);
        Component currentLine = Component.empty();
        int lineLength = 0;
        for (int i = 0; i < parts.size(); i++) {
            TextComponent part = parts.get(i);
            Style style = part.style();
            String content = part.content();

            TextComponent nextPart = i == parts.size() - 1 ? null : parts.get(i + 1);
            boolean join = nextPart != null && (part.content().endsWith(" ") || nextPart.content().startsWith(" "));

            StringBuilder lineBuilder = new StringBuilder();

            String[] words = content.split(" ");
            words = Arrays.stream(words)
                    .flatMap(word -> Arrays.stream(word.splitWithDelimiters("\n", -1)))
                    .toArray(String[]::new);
            for (int j = 0; j < words.length; j++) {
                String word = words[j];
                boolean lastWord = j == words.length - 1;
                if (word.isEmpty()) continue;
                boolean isLongEnough = lineLength != 0 && lineLength + word.length() > length;
                int newLines = StringUtils.countMatches(word, '\n') + (isLongEnough ? 1 : 0);
                for (int k = 0; k < newLines; ++k) {
                    String endOfLine = lineBuilder.toString();

                    currentLine = currentLine.append(Component.text(endOfLine).style(style));
                    wrapped.add(currentLine);

                    lineLength = 0;
                    currentLine = Component.empty().style(style);
                    lineBuilder = new StringBuilder();
                }
                boolean addSpace = (!lastWord || join) && !word.endsWith("\n");
                String cleanWord = word.replace("\n", "");
                lineBuilder.append(cleanWord).append(addSpace ? " " : "");
                lineLength += word.length() + 1;
            }
            String endOfComponent = lineBuilder.toString();
            if (!endOfComponent.isEmpty()) {
                currentLine = currentLine.append(Component.text(endOfComponent).style(style));
            }
        }

        if (lineLength > 0) {
            wrapped.add(currentLine);
        }

        return wrapped;
    }

    private static List<TextComponent> flatten(TextComponent component) {
        List<TextComponent> flattened = new ArrayList<>();

        Style enforcedState = enforceStates(component.style());
        component = component.style(enforcedState);

        Stack<TextComponent> toCheck = new Stack<>();
        toCheck.add(component);

        while (!toCheck.empty()) {
            TextComponent parent = toCheck.pop();
            if (!parent.content().isEmpty()) {
                flattened.add(parent);
            }

            for (Component child : parent.children().reversed()) {
                if (child instanceof TextComponent text) {
                    Style style = parent.style();
                    style = style.merge(child.style());
                    toCheck.add(text.style(style));
                } else {
                    toCheck.add(unsupported());
                }
            }
        }
        return flattened;
    }

    private static Style enforceStates(Style style) {
        Style.Builder builder = style.toBuilder();
        style.decorations().forEach((decoration, state) -> {
            if (state == TextDecoration.State.NOT_SET) {
                builder.decoration(decoration, false);
            }
        });
        return builder.build();
    }

    private static TextComponent unsupported() {
        return Component.text("!CANNOT WRAP!").color(NamedTextColor.DARK_RED);
    }
}
