package io.kloon.gameserver.modes.creative.menu.masks;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayout;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.commands.masks.MaskCommand;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskTypes;
import io.kloon.gameserver.util.RandUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.Arrays;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MasksSelectionMenu extends ChestMenu {
    public static final String ICON = "\uD83C\uDFAD"; // 🎭

    private final ChestMenu parent;

    public MasksSelectionMenu(ChestMenu parent) {
        super(STR."\{ICON} Masks Selection");
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        ChestLayouts.INSIDE.distribute(MaskTypes.getList(), (slot, mask) -> {
            reg(slot, new MaskPickupButton(mask));
        });

        reg().goBack(parent);
        reg(size.bottomCenter() + 1, new ClearMasksButton());
        reg(size.bottomCenter() + 2, new UndressButton());

        reg(size.last(), new MaskSelectionCommandsButton());
    }

    @Override
    public ItemStack renderButton(Player p) {
        Component name = MM."<\{MaskItem.TEXT_COLOR.asHexString()}>\{ICON} <title>Masks & Fashion";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{MaskCommand.MASKS_ALT}");
        lore.addEmpty();

        lore.wrap("<gray>Wear masks as armor pieces to filter which blocks your tools can edit.");
        lore.addEmpty();
        lore.add("<cta>Click to browse!");

        return MenuStack.ofHead(RandUtil.getRandom(MASK_HEADS)).name(name).lore(lore).build();
    }

    public static final List<String> MASK_HEADS = Arrays.asList(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNkZTk1MGYyNmY4MmUzNjM0Nzc2MTk3NmYwMmQ3ZWRjOGI5YWQ4YjkzZmRjNGFkZDNiNzZmYThmYjAyMjc3In19fQ==",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzc2M2ZlYThiNzA2MWM2OWJmZWNmMDYyY2I0YmU0ZWRmNDNjMGM3ZTk1YTY5MGFiNzlkZDJmMTNmZTE2NzE2MCJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTE5M2RkYzZiZWY1NTEzODg1MzU0YjMxZWFhZDQ1NTQ5ZDJjZWQyNTQxODA4ODQxNGM2MmE5ZTg3MzE2NjFhNSJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDNlOTNlYTUwZDFjOWU5NWM5Y2IxM2E3NzYyNGYyYzYxZjg1ZWI0MTViMGU2YjY4ZjkyYWU5NDNmMzQwMGQ0YSJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTkxZTZmZDUzZmM1ODQ4MjMwOTcyMDU1NmZmNTgwODYxODIyODVhZTgwYmMzNGIzODIxOTE2NDdkZDU0MDYxYyJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWIwNTU0MzA5MzgyZjc4N2ExM2I4NTA5ZjMyMzg3Zjc0NWExZGQ5ZGYxNjUyNjEwMWZkYzI3ZjcwZmYxM2M0YyJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjAxNzBjMmQxOTA0NmMzZjc1ZmE0MTliYzlhOGZhMGI4N2YxYWJiMGEyYmM3NjBhN2UzZjIxZTM4ODc5NmUzMyJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQyNDU0YWYyMTg1NTU0ZjJjNTFhZmYzMWUwYzA4NTJhNjhkNWIxYTJkYjdlOGZmYjM3ZTM5OWQ2NDllMDUwZiJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjAwNGIxMDIzODMyM2U3YmEzN2ExY2YwNThmZDFkZjczMmE2YTExMzQ2MzQxN2E5YWUxYTIxMDhmNTc4NTE4In19fQ==",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzhkZmVlZmI5YzcyNGVjODRlMGNkZTViOTBmMzJkMDY3YjdhM2MwMTg1YmJlZWE0ZWUxMWRmMDQ2ZjE0ZTcyNyJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdlZGZlMWIwNmQ5NzIxZGE2MWVmMjZjYWZlOGJkYWVlYmVhMDNlMDMxMDE4YmRhYjhmZGM4YWY2NjA3MGI3ZCJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWNjN2Y1YWQxZDkxYTg5MmQ2MTgyYWUxMmMxNjliMGRmOTI1MTA3NjVhMDFiNTY1ODZlMzBjOGExMjc2MDFiZiJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2RmNDU2OGU5ZTA0ZDA2NDJiNGFhZDM2OWFmNWM4NTM1ZjdiZDVhMzIyYjI5NjUyYmEzZjU0MmI3ZWE5M2JkNCJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI4OTc1MmZjY2QxNGVmY2MwNjNkNzFkOTA4MjFmZjdmYTc4MWIyZjVkNGUwNWRlMTkyZmM1ZmQ5YzVkMWYzMCJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjRlYTYxNzQ1ODIzNmIyODUzNTc4N2Y0ZmRkNTU4ZjliMzJmNTljYmVjMTIzOWI4Y2Y5ZTgwYWJhNGJmYzAxMiJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODg4Yzg0NGI0MzY0MTllOGU2YTc2Y2ZjMzIyZDVlOGZmYTg2MjFjNGJhYTk2ZjZlODdlOGVmYTA5MDk0NmZmNiJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQyOTViNzkxZGRmYjc3MWE4NWQ3ZjZjZmFkZjcxNjc5MGUxZTY5ODU4YmQ0NWMwNzM1YTE5YzZjNzMwMWZkMSJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmY1MDg1NDY3YTgzZWZjMzU2ZGM2NjQ3Nzk0NzRiY2JkM2VjM2I4Nzc2OTAxZTRkOTJkMDM5YWNhZmU0MjEwMSJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzE2MDQ1MTU3NmVlYTFiMDgxZjI1MzYwNzNmYTNkNWM1YmVjMzQ0OTQwZGUzOThlNzE0OTQ0YWU2ZTI1YTNlMiJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmJjN2Y5MmE0ZWZjZGE1MzFlMjAyNzBlNzQ2YTBhNmEzNzM1YTZhZjQ4NDNlODA4YzIyZWY3OGNjYzY5YTZhIn19fQ==",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2MzY2YwYTVjNzk5OWRhY2NhZjVmZDY4MGUwNzE1NWZhNzhkNjIwZjkzMWYwOTg1ODY1NzkzYjc5ZDMxMDI1In19fQ==",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODAzYjBlMDJiOTI4MzhlZjg4NzY0MGJiZmYxOWJjZjE0Yjk1N2UyMzViNmVhZTk3NDMzZDZlOThkN2M5NmUzZiJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmEzZDFhZmRjZjAxYTFiNDRjZGY5OTQ5ZGY3MjU3MjdhZmU5YmMyNzdkYjUzMmRhZjhlZjM5NzEwZTRlZDI3In19fQ==",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ2ZThiN2UxMzE2NTJjYjgxYTc5NzU4ZGJjMTNlZGExYmRlODdlODYyY2QzM2NiMWYwMGY4ODgxMDU0MDJlYyJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTc2NWI3YmVhNzMxZWU0ZGRiN2I5ZTQ3OGU3Mjg3NzMxOWFjYjY3ZjczMWJmMmY2ZDBmZmIwNmMyMWZjZmE1YSJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJjYTZmZTQ1N2FlMjhiOGM0NjdiNTAxZWMwYjAxOTQ1NDIyOGRkNWViYjgyZDZhMmQ2YTZhODU0NDhhZGRhYSJ9fX0=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTAwZmU2Y2Y5YTg5MDM1MzE0NTgzOGM4MzE4NzhiZTc0ZWFlYjg1ZTY4MTJjZGVhYjc5ODQ0NTIzNzk1MWZjYSJ9fX0="
    );
}
