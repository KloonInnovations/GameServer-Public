package io.kloon.gameserver.modes.creative.masks;

import io.kloon.gameserver.modes.creative.masks.impl.SolidMask;
import io.kloon.gameserver.modes.creative.masks.impl.blocktype.BlockTypeMask;
import io.kloon.gameserver.modes.creative.masks.impl.OverlayTypeMask;
import io.kloon.gameserver.modes.creative.masks.impl.exactblock.ExactBlockMask;
import io.kloon.gameserver.modes.creative.masks.impl.inselection.InsideSelectionMask;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.ProximityMask;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MaskTypes {
    private static final Map<String, MaskType<?>> BY_DB_KEY = new HashMap<>();
    private static final List<MaskType<?>> LIST = new ArrayList<>();

    public static final BlockTypeMask BLOCK_TYPE = reg(new BlockTypeMask("block_type"));
    public static final ExactBlockMask EXACT_BLOCK = reg(new ExactBlockMask("exact_block"));
    public static final OverlayTypeMask OVERLAY = reg(new OverlayTypeMask("overlay"));
    public static final SolidMask SOLID = reg(new SolidMask("solid"));
    public static final InsideSelectionMask INSIDE_SELECTION = reg(new InsideSelectionMask("inside_selection"));
    public static final ProximityMask PROXIMITY = reg(new ProximityMask("proximity"));

    private static <T extends MaskType<?>> T reg(T mask) {
        BY_DB_KEY.put(mask.getDbKey(), mask);
        LIST.add(mask);
        return mask;
    }

    @Nullable
    public static MaskType<?> get(String dbKey) {
        return BY_DB_KEY.get(dbKey);
    }

    public static List<MaskType<?>> getList() {
        return LIST;
    }
}
