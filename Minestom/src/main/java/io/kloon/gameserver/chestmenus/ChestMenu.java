package io.kloon.gameserver.chestmenus;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.autoupdate.AutoUpdateMenu;
import io.kloon.gameserver.chestmenus.autoupdate.MenuAutoUpdate;
import io.kloon.gameserver.chestmenus.breadcrumb.BreadcrumbButton;
import io.kloon.gameserver.chestmenus.util.CommonReg;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public abstract class ChestMenu implements ChestButton {
    private static final Logger LOG = LoggerFactory.getLogger(ChestMenu.class);

    protected final ChestSize size;
    private Function<Player, Component> titleFunction;

    private boolean loaded = false;
    private final Map<Integer, ChestButton> buttons = new HashMap<>();

    private BreadcrumbButton breadcrumbs = new BreadcrumbButton(Material.BREAD);
    private static final PlayerTickCooldownMap clickCd = new PlayerTickCooldownMap(1);

    public ChestMenu(String title) {
        this(title, ChestSize.SIX);
    }

    public ChestMenu(String title, ChestSize size) {
        this.size = size;
        this.titleFunction = _ -> MM.process(StringTemplate.of(title));
    }

    protected Function<Player, Component> getTitleFunction() {
        return titleFunction;
    }

    protected void setTitleFunction(Function<Player, Component> titleFunction) {
        this.titleFunction = titleFunction;
    }

    protected void setBreadcrumbs(Material icon, String title, String actionMM) {
        this.breadcrumbs = new BreadcrumbButton(icon).with(title, actionMM);
        setTitleFunction(p -> MM."\{title}");
    }

    protected void setBreadcrumbs(ChestMenu parent, String title, String actionMM) {
        if (parent == null) {
            setBreadcrumbs(Material.BREAD, title, actionMM);
        } else {
            this.breadcrumbs = parent.getBreadcrumbs().with(title, actionMM);
            setTitleFunction(breadcrumbs::generateTitle);
        }
    }

    public BreadcrumbButton getBreadcrumbs() {
        return breadcrumbs;
    }

    public final void load() {
        if (loaded) return;
        loaded = true;

        try {
            registerButtons();
        } catch (Throwable t) {
            LOG.error(STR."Error registering buttons in \{getClass()}", t);
        }
    }

    public final ChestMenu reload() {
        buttons.clear();
        this.loaded = false;
        load();
        return this;
    }

    protected abstract void registerButtons();

    protected final CommonReg reg() {
        return new CommonReg(this, this::reg);
    }

    protected final void reg(int slot, Function<Integer, ChestButton> slotToButton) {
        ChestButton button = slotToButton.apply(slot);
        reg(slot, button);
    }

    protected final void reg(int slot, ChestButton button) {
        if (slot >= size.getMaxSlots()) {
            LOG.warn(STR."Tried putting button \{button.getClass()} outside of \{size.name()} at slot \{slot}");
            return;
        }

        buttons.put(slot, button);
    }

    @Nullable
    public final ChestButton getButton(int slot) {
        return buttons.get(slot);
    }

    public final void display(CommandSender sender) {
        if (sender instanceof Player player) {
            display(player);
        } else {
            sender.sendMessage(MM."<red>This command opens a menu which can only be seen by players!");
        }
    }

    public final ChestMenu display(Player player) {
        ChestMenu redirect = getRedirectOnDisplay(player);
        if (redirect != null) {
            return redirect.display(player);
        }

        Component title = generateTitle(player);

        load();

        ChestMenuInv inventory = new ChestMenuInv(size.getInventoryType(), title, this);

        render(inventory, player);

        player.openInventory(inventory);

        if (this instanceof AutoUpdateMenu autoUpdate) {
            TaskSchedule period = autoUpdate.getAutoUpdatePeriod();
            player.scheduler().scheduleTask(new MenuAutoUpdate(inventory, buttons, player, period), period);
        }

        return this;
    }

    public final void handleClickMenuInventory(InventoryPreClickEvent event) {
        Player clicker = event.getPlayer();
        int slot = event.getSlot();
        Click eventClick = event.getClick();

        if (!clickCd.get(clicker).cooldownIfPossible()) {
            return;
        }

        ChestButton button = buttons.get(slot);
        if (button != null) {
            try {
                ButtonClick click = new ButtonClick(clicker, eventClick);
                button.clickButton(clicker, click);
            } catch (Throwable t) {
                LOG.error(STR."Error clicking on button \{button.getClass()} by \{clicker.getUsername()}", t);
                clicker.sendMessage(MM."<red>Oops, there was an error when pressing that buttton!");
            }
        }
    }

    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {

    }

    @Nullable
    public ChestMenu getRedirectOnDisplay(Player player) {
        return null;
    }

    public final void render(Inventory inventory, Player player) {
        for (int slot = 0; slot < size.getMaxSlots(); ++slot) {
            ChestButton button = buttons.get(slot);
            ItemStack renderedItem = button == null
                    ? ItemStack.AIR
                    : render(slot, button, player);
            inventory.setItemStack(slot, renderedItem);
        }
        inventory.setTitle(generateTitle(player));
    }

    public final ItemStack render(int slot, @Nullable ChestButton button, Player player) {
        if (button == null) {
            return ItemStack.AIR;
        }

        try {
            ItemStack renderedButton = button.renderButton(player);
            if (renderedButton == null) {
                renderedButton = new ItemBuilder2(Material.BARRIER)
                        .name(MM."<red>Default: \{button.getClass().getSimpleName()}")
                        .lore(MM_WRAP."<red>This needs to be edited.").build();
            }
            return renderedButton;
        } catch (Throwable t) {
            LOG.error(STR."Error rendering \{button.getClass()} in \{getClass()} at slot \{slot}", t);
            return new ItemBuilder2(Material.BARRIER)
                    .name(MM."<red>Error!")
                    .lore(Arrays.asList(
                            MM."<gray>There was an error while",
                            MM."<gray>rendering this button!"
                    )).build();
        }
    }

    public final Component generateTitle(Player player) {
        try {
            return titleFunction.apply(player);
        } catch (Throwable t) {
            LOG.error(STR."Error generating title in chest menu \{getClass()}", t);
            return Component.text("TITLE ERROR").color(NamedTextColor.RED);
        }
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        display(player);
    }

    public ChestSize size() {
        return size;
    }

    @Override
    public ItemStack renderButton(Player player) {
        List<Component> lore = MM_WRAP."<gray>This is the default text for a menu. Someone should probably override this.";
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to open!");

        Component title = generateTitle(player);

        return new ItemBuilder2(Material.CHERRY_CHEST_BOAT)
                .name(title.color(NamedTextColor.GREEN))
                .lore(lore)
                .build();
    }
}
