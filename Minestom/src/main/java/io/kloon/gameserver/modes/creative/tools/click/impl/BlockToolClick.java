package io.kloon.gameserver.modes.creative.tools.click.impl;

import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.ToolClickSide;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BlockToolClick extends ToolClick {
    private final Vec blockPos;
    private final Vec cursorPos;

    public BlockToolClick(ToolClickSide side, ItemStack item, Point blockPos, @Nullable Vec cursorPos) {
        super(side, item);
        this.blockPos = Vec.fromPoint(blockPos);
        this.cursorPos = cursorPos;
    }

    public Vec getBlockPos() {
        return blockPos;
    }

    @Nullable
    public Vec getCursorPos() {
        return cursorPos;
    }
}
