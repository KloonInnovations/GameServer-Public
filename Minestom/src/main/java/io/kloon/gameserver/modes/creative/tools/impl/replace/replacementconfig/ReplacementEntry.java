package io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig;

import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.minestom.server.instance.block.Block;

public record ReplacementEntry(Block replacing, CreativePattern whatToReplaceWith, boolean replaceOnExactState) {
    public String labelMM() {
        String replacingLabel = new TinkeredBlock(replacing, replaceOnExactState).getNameMM();
        return STR."\{replacingLabel} <dark_gray>➜ \{whatToReplaceWith.labelMM()}";
    }
}
