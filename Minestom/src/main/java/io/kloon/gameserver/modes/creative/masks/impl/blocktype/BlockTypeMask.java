package io.kloon.gameserver.modes.creative.masks.impl.blocktype;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskWorkCache;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.block.MaskWithBlock;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class BlockTypeMask extends MaskType<BlockTypeMask.Data> {
    public BlockTypeMask(String dbKey) {
        super(dbKey, Data.class, Data::new);
    }

    @Override
    public ItemBuilder2 getIcon() {
        return new ItemBuilder2(Material.CHORUS_PLANT);
    }

    @Override
    public String getCommandLabel() {
        return "block";
    }

    @Override
    public String getName() {
        return "Block Type";
    }

    @Override
    public @Nullable String renderNameAppendMM(MaskWithData<Data> mask) {
        Block block = mask.data().getBlock();
        return mask.negated()
                ? STR."<white>NOT \{BlockFmt.getName(block)}"
                : STR."<white>\{BlockFmt.getName(block)}";
    }

    @Override
    public Lore getDatalessDescription() {
        return new Lore().wrap("<gray>Must match a block type.");
    }

    @Override
    public Lore getLore(Data data, boolean negated) {
        Lore lore = new Lore();
        if (negated) {
            lore.add(MM."<gray>Blocks must NOT be of this type.");
        } else {
            lore.add(MM."<gray>Blocks must be of this type.");
        }
        lore.addEmpty();
        lore.add(MM."<gray>Block: <white>\{BlockFmt.getName(data.getBlock())}");
        return lore;
    }

    @Override
    public Lore getConditionBulletPoint(Data data, boolean negated, boolean onlyPoint) {
        Lore lore = new Lore();
        if (negated) {
            lore.add(MM."\{PREFIX} Block is NOT <white>\{BlockFmt.getName(data.getBlock())}<gray>.");
        } else {
            lore.add(MM."\{PREFIX} Block is <white>\{BlockFmt.getName(data.getBlock())}<gray>.");
        }
        return lore;
    }

    @Override
    public boolean matches(MaskWorkCache workCache, Data data, Block.Getter instance, Point blockPos, Block block) {
        return block.id() == data.block.id();
    }

    @Override
    public List<Command> createCommands() {
        return List.of(new BlockTypeMaskCommand(this));
    }

    @Override
    public @Nullable ChestMenu createMaskMenu(EditMaskItemMenu parent, MaskWithData<Data> mask) {
        return new BlockTypeMaskMenu(parent, mask);
    }

    public static class Data implements MaskWithBlock {
        private Block block = Block.STONE;

        public Block getBlock() {
            return block;
        }

        public void setBlock(Block block) {
            this.block = block;
        }
    }
}
