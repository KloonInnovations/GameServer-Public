package io.kloon.gameserver.modes.creative.tools.generics;

import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import net.minestom.server.item.ItemStack;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class NotImplementedTool extends NoDataTool {
    private final String toolTypeDbKey;
    private final String toolData;

    public NotImplementedTool(String toolTypeDbKey) {
        this(toolTypeDbKey, null);
    }

    public NotImplementedTool(String toolTypeDbKey, @Nullable String toolData) {
        super(CreativeToolType.UNKNOWN);
        this.toolTypeDbKey = toolTypeDbKey;
        this.toolData = toolData;
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {

    }

    @Override
    public ItemStack renderItem() {
        ItemBuilder2 builder = new ItemBuilder2(CreativeToolType.UNKNOWN.getMaterial())
                .tag(TOOL_TYPE_TAG, toolTypeDbKey)
                .tag(UNSTACKABLE, new ObjectId().toHexString())
                .hideFlags()
                .name(MM."<red>Unknown Tool")
                .lore(MM_WRAP."<gray>This is weird but you're holding an unrecognized tool with id <white>\{toolTypeDbKey}<gray>.");

        if (toolData != null) {
            builder.tag(TOOL_DATA, toolData);
        }

        return builder.build();
    }
}
