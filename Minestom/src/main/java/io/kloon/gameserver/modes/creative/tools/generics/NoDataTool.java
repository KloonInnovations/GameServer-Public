package io.kloon.gameserver.modes.creative.tools.generics;

import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class NoDataTool extends CreativeTool<Void, Void> {
    public NoDataTool(CreativeToolType type) {
        super(type, new ToolDataDef<>(() -> null, Void.class, () -> null, Void.class));
    }

    @Override
    public @Nullable ItemStack renderOverride(Void unused, Void unused2) {
        return renderItem();
    }

    public ItemStack renderItem() {
        return toolBuilder(null).lore(getItemLore(null, null)).build();
    }
}
