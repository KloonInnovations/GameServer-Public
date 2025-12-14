package io.kloon.gameserver.modes.creative.buildpermits.menu.create.duration;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.buildpermits.duration.EphemeralPermit;
import io.kloon.gameserver.modes.creative.buildpermits.duration.InfinitePermit;
import io.kloon.gameserver.modes.creative.buildpermits.duration.PermitDuration;
import io.kloon.gameserver.modes.creative.buildpermits.duration.TimedPermit;
import io.kloon.gameserver.modes.creative.buildpermits.menu.create.CreatePermitMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PermitDurationMenu extends ChestMenu {
    private final CreatePermitMenu parent;

    public PermitDurationMenu(CreatePermitMenu parent) {
        super("Permit Duration", ChestSize.FOUR);

        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        List<DurIcon> hardcodedDurations = Arrays.asList(
                new DurIcon(Material.PINK_STAINED_GLASS, new EphemeralPermit()),
                new DurIcon(Material.ORANGE_TERRACOTTA, new TimedPermit(Duration.ofHours(2))),
                new DurIcon(Material.YELLOW_TERRACOTTA, new TimedPermit(Duration.ofDays(1))),
                new DurIcon(Material.LIME_TERRACOTTA, new TimedPermit(Duration.ofDays(7))),
                new DurIcon(Material.BLUE_TERRACOTTA, new TimedPermit(Duration.ofDays(30))),
                new DurIcon(Material.BEDROCK, new InfinitePermit())
        );

        int slot = 10;
        for (int i = 0; i < hardcodedDurations.size(); ++i) {
            DurIcon durIcon = hardcodedDurations.get(i);
            reg(slot++, new PermitDurationButton(parent, durIcon.icon, durIcon.durationn));
        }
        reg(slot++, new CustomDurationButton(parent, this));

        reg().goBack(parent);
    }

    record DurIcon(Material icon, PermitDuration durationn) {}

    @Override
    public ItemStack renderButton(Player player) {
        PermitDuration duration = parent.getState().getDuration();

        Component name;
        Lore lore = new Lore();

        if (duration == null) {
            name = MM."<title>Permit Duration";
            lore.wrap("<gray>Choose how long this permit is valid for.");
            lore.addEmpty();
            lore.add("<cta>Click to pick!");
        } else {
            name = MM."<title>\{duration.formattedMM()}";
            lore.add(duration.lore());
            lore.addEmpty();
            lore.add("<cta>Click to edit!");
        }

        return MenuStack.of(Material.CLOCK, name, lore);
    }
}
