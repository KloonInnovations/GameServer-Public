package io.kloon.gameserver.modes.creative.menu.worldadmin.time;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.autoupdate.AutoUpdateButton;
import io.kloon.gameserver.chestmenus.autoupdate.AutoUpdateMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.world.TimeCommand;
import io.kloon.gameserver.modes.creative.menu.worldadmin.time.preset.PresetTime;
import io.kloon.gameserver.modes.creative.menu.worldadmin.time.preset.PresetTimeButton;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WorldTimeMenu extends ChestMenu implements AutoUpdateMenu, AutoUpdateButton {
    public static final String ICON = "⏰";

    private final ChestMenu parent;

    private long renderedTime = 0;

    public WorldTimeMenu(ChestMenu parent) {
        super("World Time");
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        reg(11, new PresetTimeButton(PresetTime.MORNING, Material.CARROT));
        reg(12, new PresetTimeButton(PresetTime.DAY, Material.SUNFLOWER));
        reg(13, new PresetTimeButton(PresetTime.EVENING, Material.MAGMA_CREAM));
        reg(14, new PresetTimeButton(PresetTime.NIGHT, Material.CHORUS_FRUIT));
        reg(15, new CustomTimeButton());

        reg(30, new CurrentTimeButton());
        reg(32, TimeRateButton::new);

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<green>\{ICON} <title>World Time";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{TimeCommand.LABEL}");
        lore.addEmpty();
        lore.wrap("<gray>Configure the time and how it flows in the world.");
        lore.addEmpty();

        this.renderedTime = player.getInstance().getTime();
        lore.add(MM."<gray>Current time: <green>\{renderedTime % 24_000}");

        double timeRate = player.getInstance().getWorldStorage().getTime().getTimeRate();
        String rateFmt = timeRate == 0
                ? "Frozen!"
                : STR."\{NumberFmt.TWO_DECIMAL.format(timeRate * 20)}/s";
        lore.add(MM."<gray>Rate: <aqua>\{rateFmt}");

        lore.addEmpty();

        lore.add("<cta>Click to edit!");

        return MenuStack.of(Material.CLOCK, name, lore);
    }

    @Override
    public boolean shouldReloadMenu() {
        return false;
    }

    @Override
    public boolean shouldRerender(Player player) {
        return player.getInstance().getTime() != renderedTime;
    }
}
