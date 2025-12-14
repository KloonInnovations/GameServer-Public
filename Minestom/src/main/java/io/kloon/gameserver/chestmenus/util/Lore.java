package io.kloon.gameserver.chestmenus.util;

import io.kloon.gameserver.MiniMessageTemplate;
import io.kloon.gameserver.minestom.components.ComponentWrapper;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class Lore {
    private final List<Component> lines = new ArrayList<>();

    public Lore add(Component line) {
        lines.add(line);
        return this;
    }

    public Lore add(String mm) {
        lines.add(MiniMessageTemplate.toComponent(mm));
        return this;
    }

    public Lore addEmpty() {
        lines.add(Component.empty());
        return this;
    }

    public Lore add(List<Component> components) {
        lines.addAll(components);
        return this;
    }

    public Lore add(Lore lore) {
        this.lines.addAll(new ArrayList<>(lore.lines));
        return this;
    }

    public Lore wrap(Component component) {
        lines.addAll(ComponentWrapper.wrap(component, MiniMessageTemplate.WRAP_LENGTH));
        return this;
    }

    public Lore wrap(String mm) {
        Component component = MiniMessageTemplate.toComponent(mm);
        return wrap(component);
    }

    public List<Component> asList() {
        return lines;
    }

    public Component asComponent() {
        Component base = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            base = base.append(line);
            boolean last = i == lines.size() - 1;
            if (!last) {
                base = base.appendNewline();
            }
        }
        return base;
    }
}
