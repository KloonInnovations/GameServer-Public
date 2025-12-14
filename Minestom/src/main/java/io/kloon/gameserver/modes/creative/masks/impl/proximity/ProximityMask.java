package io.kloon.gameserver.modes.creative.masks.impl.proximity;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.utils.PointFmt;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.command.ProximityMaskCommand;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.menu.ProximityMaskMenu;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.util.ManhattanIteration;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.util.SwirlIteration;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskWorkCache;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.util.coordinates.Axis;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.masks.impl.proximity.ProximityMask.*;

public class ProximityMask extends MaskType<Data> {
    public ProximityMask(String dbKey) {
        super(dbKey, Data.class, Data::new);
    }

    @Override
    public ItemBuilder2 getIcon() {
        return MenuStack.of(Material.CAMPFIRE);
    }

    @Override
    public String getName() {
        return "Proximity";
    }

    @Override
    public Lore getDatalessDescription() {
        return new Lore().wrap("<gray>Block has nearby non-air block.");
    }

    @Override
    public Lore getLore(Data data, boolean negated) {
        Lore lore = new Lore();
        if (negated) {
            lore.add("<gray>Block must NOT have nearby block.");
        } else {
            lore.add("<gray>Block must have nearby non-air block.");
        }
        lore.addEmpty();
        lore.add(MM."<gray>Range: <green>\{data.getRange()} blocks");
        lore.add(MM."<dark_gray>Manhattan range!");
        return lore;
    }

    @Override
    public Lore getConditionBulletPoint(Data data, boolean negated, boolean onlyPoint) {
        ProximityAxis axis = data.getAxis();
        if (axis == ProximityAxis.ALL) {
            if (negated) {
                return new Lore().wrap(MM."\{PREFIX} Block has NO non-air block within <green>\{data.range} blocks<gray>.");
            } else {
                return new Lore().wrap(MM."\{PREFIX} Block has nearby non-air block within <green>\{data.range} blocks<gray>.");
            }
        } else if (axis == ProximityAxis.AUTO) {
            if (negated) {
                return new Lore().wrap(MM."\{PREFIX} Block has NO non-air block within <green>\{data.range} blocks<gray>, auto axis.");
            } else {
                return new Lore().wrap(MM."\{PREFIX} Block has nearby non-air block within <green>\{data.range} blocks<gray>, auto axis.");
            }
        } else {
            if (negated) {
                return new Lore().wrap(MM."\{PREFIX} Block has NO non-air block within <green>\{data.range} blocks<gray> on the <gold>\{axis.label()} axis<gray>.");
            } else {
                return new Lore().wrap(MM."\{PREFIX} Block has nearby non-air block within <green>\{data.range} blocks<gray> on the <gold>\{axis.label()} axis<gray>.");
            }
        }
    }

    @Override
    public boolean matches(MaskWorkCache workCache, Data data, Block.Getter instance, Point blockPos, Block block) {
        if (data.onlyEditAir && !block.isAir()) {
            return false;
        }


        ProximityAxis proximityAxis = data.getAxis();
        if (proximityAxis == ProximityAxis.ALL) {
            double nearestNonAirSq = workCache.proximity3d().computeNearestNonAirWithinSq(instance, blockPos, data.getRange());
            return nearestNonAirSq <= data.getRangeSq();
        }

        Axis axis;
        if (proximityAxis == ProximityAxis.AUTO) {
            Vec dir = workCache.lookDir();
            axis = CardinalDirection.closestDir(dir).axis();
        } else {
            axis = proximityAxis.getAxis();
        }

        double nearestNonAirSq = computeNearestNonAir2d(instance, blockPos, data.getRange(), axis);
        return nearestNonAirSq <= data.getRange();
    }

    private double computeNearestNonAir2d(Block.Getter instance, Point center, int radius, Axis axis) {
        AtomicReference<Point> blockPos = new AtomicReference<>();
        SwirlIteration.iterate(center, radius, axis, point -> {
            Block block = instance.getBlock(point);
            if (!block.isAir() && ManhattanIteration.manhanttanDistance(center, point) <= radius) {
                blockPos.set(point);
                return false;
            }
            return true;
        });
        Point point = blockPos.get();
        return point == null ? Double.MAX_VALUE : ManhattanIteration.manhanttanDistance(center, point);
    }

    @Override
    public List<Command> createCommands() {
        return List.of(new ProximityMaskCommand(this));
    }

    @Override
    public ChestMenu createMaskMenu(EditMaskItemMenu parent, MaskWithData<Data> mask) {
        return new ProximityMaskMenu(parent, mask);
    }

    public static class Data {
        private String axis;
        private int range = 1;
        private int rangeSq = 1;
        private boolean onlyEditAir = true;

        public ProximityAxis getAxis() {
            return ProximityAxis.BY_DB_KEY.get(axis, ProximityAxis.ALL);
        }

        public void setAxis(ProximityAxis axis) {
            this.axis = axis.getDbKey();
        }

        public void setRange(int range) {
            this.range = range;
            this.rangeSq = range * range;
        }

        public int getRange() {
            return range;
        }

        public int getRangeSq() {
            return rangeSq;
        }

        public boolean isOnlyEditAir() {
            return onlyEditAir;
        }

        public void setOnlyEditAir(boolean onlyEditAir) {
            this.onlyEditAir = onlyEditAir;
        }
    }
}
