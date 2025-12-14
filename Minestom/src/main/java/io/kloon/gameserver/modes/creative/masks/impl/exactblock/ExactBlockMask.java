package io.kloon.gameserver.modes.creative.masks.impl.exactblock;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskWorkCache;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.block.MaskWithBlock;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.masks.impl.exactblock.ExactBlockMask.*;

public class ExactBlockMask extends MaskType<Data> {
    public ExactBlockMask(String dbKey) {
        super(dbKey, Data.class, Data::new);
    }

    @Override
    public ItemBuilder2 getIcon() {
        return new ItemBuilder2(Material.CHORUS_FLOWER);
    }

    @Override
    public String getCommandLabel() {
        return "block_exact";
    }

    @Override
    public String getName() {
        return "Exact Block";
    }

    @Override
    public Lore getDatalessDescription() {
        return new Lore().wrap("<gray>Must match an exact block, meaning all properties must be equal.");
    }

    @Override
    public Lore getLore(Data data, boolean negated) {
        Lore lore = new Lore();
        if (negated) {
            lore.add(MM."<gray>Blocks must NOT be the exact same.");
        } else {
            lore.add(MM."<gray>Blocks must be the exact same.");
        }
        lore.addEmpty();

        TinkeredBlock tinkered = new TinkeredBlock(data.getBlock(), true);
        lore.add(MM."<gray>Block: \{tinkered.getNameMM()}");

        return lore;
    }

    @Override
    public Lore getConditionBulletPoint(Data data, boolean negated, boolean onlyPoint) {
        Lore lore = new Lore();
        TinkeredBlock tinkered = new TinkeredBlock(data.getBlock(), true);
        if (negated) {
            lore.add(MM."\{PREFIX} Block is NOT <white>\{tinkered.getNameMM()}<gray>.");
        } else {
            lore.add(MM."\{PREFIX} Block is <white>\{tinkered.getNameMM()}<gray>.");
        }
        return lore;
    }

    @Override
    public boolean matches(MaskWorkCache workCache, Data data, Block.Getter instance, Point blockPos, Block block) {
        return block == data.block;
    }

    @Override
    public List<Command> createCommands() {
        return List.of(new ExactBlockMaskCommand(this));
    }

    @Override
    public ChestMenu createMaskMenu(EditMaskItemMenu parent, MaskWithData<Data> mask) {
        return new ExactBlockMaskMenu(parent, mask);
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
