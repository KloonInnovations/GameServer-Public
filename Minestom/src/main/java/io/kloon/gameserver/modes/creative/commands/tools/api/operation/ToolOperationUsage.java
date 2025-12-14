package io.kloon.gameserver.modes.creative.commands.tools.api.operation;

import io.kloon.gameserver.chestmenus.util.Lore;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public record ToolOperationUsage(
        String labelMM,
        Lore description
) {
    public ToolOperationUsage(String labelMM, String descriptionMM) {
        this(labelMM, new Lore().wrap("<gray>" + descriptionMM));
    }

    public Lore lore(String commandName) {
        Lore lore = new Lore();
        lore.add(MM."<dark_gray>\uD83D\uDDAE <green>/\{commandName} \{labelMM}"); // 🖮
        lore.add(description);
        return lore;
    }
}
