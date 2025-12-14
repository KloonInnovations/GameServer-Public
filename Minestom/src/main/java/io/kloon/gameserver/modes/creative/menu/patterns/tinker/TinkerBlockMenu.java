package io.kloon.gameserver.modes.creative.menu.patterns.tinker;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.builtin.ConfirmPaddingButton;
import io.kloon.gameserver.chestmenus.builtin.GoBackButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayout;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleButton;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.*;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class TinkerBlockMenu extends ChestMenu {
    private final ChestMenu parent;
    private final Block block;
    private final Block defaultBlock;

    private CreativeConsumer<Block> onEdit = null;
    private CreativeConsumer<Block> onComplete = null;

    private static final Map<Block, List<PropertyOptions>> OPTIONS_BY_BLOCK = new HashMap<>();

    private final Map<String, Cycle<PropertyOption>> cycleByProperty = new LinkedHashMap<>();

    public TinkerBlockMenu(ChestMenu parent, Block block) {
        super("Tinker");
        this.parent = parent;
        this.block = block;
        this.defaultBlock = block.defaultState();
        setBreadcrumbs(parent, STR."Tinker (\{BlockFmt.getName(block)})", "Tinkering block...");
    }

    public TinkerBlockMenu withOnEdit(CreativeConsumer<Block> onEdit) {
        this.onEdit = onEdit;
        return this;
    }


    public TinkerBlockMenu withOnComplete(CreativeConsumer<Block> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    @Override
    protected void registerButtons() {
        List<PropertyOptions> options = getOptionsForBlock(defaultBlock);

        Map<String, Collection<String>> properties = defaultBlock.propertyOptions();
        cycleByProperty.clear();
        properties.forEach((key, values) -> {
            Cycle<PropertyOption> cycle = createCycle(key, values);
            PropertyOption selected = getOptionForBlock(key, block);
            cycle.select(selected);
            cycleByProperty.put(key, cycle);
        });

        int confirmSlot = size.bottomCenter();

        ChestLayout layout = ChestLayouts.spaceOut8(properties.size());
        layout.distribute(options, (slot, prop) -> {
            Cycle<PropertyOption> cycle = cycleByProperty.get(prop.key());
            CycleButton<PropertyOption> button = new CycleButton<>(cycle, slot)
                    .withTitle(MM."<title>Property: <white>\"\{prop.key()}\"")
                    .withDescription(new Lore().add(MM."<dark_gray>of \{BlockFmt.getName(block)}").asList())
                    .withOnClick((p, opt) -> {
                        ChestMenuInv.rerenderButton(confirmSlot, p);
                        ChestMenuInv.rerenderButton(confirmSlot + 2, p);
                        if (onEdit != null) {
                            onEdit.accept((CreativePlayer) p, generateTinkeredBlock());
                        }
                    })
                    .withIcon(PropertyIcons.get(block, prop.key()));
            reg(slot, button);
        });

        reg(confirmSlot - 2, new GoBackButton(parent));
        if (parent != null) {
            reg().breadcrumbs();
        }

        if (onComplete == null) {
            reg(confirmSlot, new PickupTinkerBlockButton(this));
        } else {
            reg(confirmSlot - 1, new ConfirmPaddingButton());
            reg(confirmSlot + 1, new ConfirmPaddingButton());
            reg(confirmSlot, new ChestButton() {
                @Override
                public void clickButton(Player p, ButtonClick click) {
                    CreativePlayer player = (CreativePlayer) p;
                    Block tinkered = generateTinkeredBlock();
                    onComplete.accept(player, tinkered);
                }

                @Override
                public ItemStack renderButton(Player player) {
                    Component name = MM."<title>Confirm Tinker";

                    Lore lore = new Lore();
                    lore.add(MM."<dark_gray>for \{BlockFmt.getName(block)}");
                    lore.addEmpty();

                    cycleByProperty.forEach((property, cycle) -> {
                        lore.add(MM."<title>\{property}: <white>\{cycle.getSelected().label()}");
                    });
                    lore.addEmpty();
                    lore.add("<cta>Click to use tinkered block!");

                    Material icon = block.registry().material();
                    if (icon == null) {
                        icon = Material.GREEN_TERRACOTTA;
                    }
                    return MenuStack.of(icon, name, lore);
                }
            });
            reg(confirmSlot + 2, new PickupTinkerBlockButton(this));
        }
    }

    public Block generateTinkeredBlock() {
        Map<String, String> properties = new HashMap<>(cycleByProperty.size());
        cycleByProperty.forEach((key, cycle) -> properties.put(key, cycle.getSelected().value));
        return block.withProperties(properties);
    }

    private static List<PropertyOptions> getOptionsForBlock(Block block) {
        return OPTIONS_BY_BLOCK.computeIfAbsent(block, b -> {
            Map<String, Collection<String>> fromRegistry = b.propertyOptions();
            List<PropertyOptions> list = new ArrayList<>(fromRegistry.size());
            fromRegistry.forEach((key, values) -> list.add(new PropertyOptions(key, new ArrayList<>(values))));
            return list;
        });
    }

    private Cycle<PropertyOption> createCycle(String key, Collection<String> values) {
        List<PropertyOption> options = values.stream()
                .map(PropertyOption::new)
                .sorted(Comparator.comparingInt(opt -> {
                    String defaultValue = defaultBlock.getProperty(key);
                    return opt.value.equals(defaultValue) ? -1 : 1;
                }))
                .toList();
        return new Cycle<>(options);
    }

    record PropertyOption(String value) implements CycleLabelable {
        public String label() {
            return value;
        }
    }

    private PropertyOption getOptionForBlock(String key, Block block) {
        String value = block.getProperty(key);
        return new PropertyOption(value);
    }
}
