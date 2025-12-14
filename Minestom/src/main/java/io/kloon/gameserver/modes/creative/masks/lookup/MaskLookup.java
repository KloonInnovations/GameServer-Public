package io.kloon.gameserver.modes.creative.masks.lookup;

import io.kloon.gameserver.modes.creative.masks.*;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

public class MaskLookup {
    private static final Logger LOG = LoggerFactory.getLogger(MaskLookup.class);

    private final List<MaskItem> maskItems;
    private final MaskWorkCache workCache;

    private boolean ignoreBlockMatch = false;

    public MaskLookup(List<MaskItem> maskItems, MaskWorkCache workCache) {
        this.maskItems = maskItems;
        this.workCache = workCache;
    }

    public void setIgnoreBlockMatch(boolean ignoreBlockMatch) {
        this.ignoreBlockMatch = ignoreBlockMatch;
    }

    public MaskWorkCache getWorkCache() {
        return workCache;
    }

    public boolean isIgnored(Block.Getter instance, Point blockPos, Block block) {
        try {
            for (MaskItem maskItem : maskItems) {
                boolean okay = isOkay(maskItem, instance, blockPos, block);
                if (!okay) {
                    return true;
                }
            }
        } catch (Throwable t) {
            LOG.error("Error in mask lookup", t);
            return true;
        }
        return false;
    }

    public boolean isPassthrough() {
        return maskItems.isEmpty();
    }

    private boolean isOkay(MaskItem maskItem, Block.Getter instance, Point blockPos, Block block) {
        Stream<MaskWithData<?>> masks = maskItem.getMasks().stream();
        MasksUnion union = maskItem.getUnion();
        return switch (union) {
            case OR -> masks.anyMatch(m -> isOkay(m, instance, blockPos, block));
            case AND -> masks.allMatch(m -> isOkay(m, instance, blockPos, block));
        };
    }

    private boolean isOkay(MaskWithData<?> mask, Block.Getter instance, Point blockPos, Block block) {
        MaskType type = mask.type();
        if (ignoreBlockMatch && type == MaskTypes.BLOCK_TYPE) {
            return true;
        }

        if (type.skip(workCache, mask.data(), block)) {
            return true;
        }

        boolean matches = type.matches(workCache, mask.data(), instance, blockPos, block);
        if (mask.negated()) {
            matches = !matches;
        }
        return matches;
    }
}
