package io.kloon.gameserver.modes.creative.menu.history;

import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.StaticButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.history.HistoryCommand;
import io.kloon.gameserver.modes.creative.history.ChangeRecord;
import io.kloon.gameserver.modes.creative.history.History;
import io.kloon.gameserver.modes.creative.history.OngoingChange;
import io.kloon.gameserver.modes.creative.menu.history.actions.RedoButton;
import io.kloon.gameserver.modes.creative.menu.history.actions.UndoButton;
import io.kloon.gameserver.modes.creative.menu.history.audit.AuditHistoryMenu;
import io.kloon.gameserver.modes.creative.menu.history.records.FutureRecordButton;
import io.kloon.gameserver.modes.creative.menu.history.records.OngoingButton;
import io.kloon.gameserver.modes.creative.menu.history.records.PastRecordButtonWithUndo;
import io.kloon.gameserver.modes.creative.menu.tools.ToolPickupButton;
import io.kloon.gameserver.modes.creative.tools.impl.history.HistoryTool;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class HistoryMenu extends ChestMenu {
    public static final String ICON = "\uD83D\uDD70"; // 🕰

    private final HistoryTool historyTool;
    private final ChestMenu parent;
    private final CreativeInstance instance;
    private final History history;

    public HistoryMenu(HistoryTool historyTool, ChestMenu parent, CreativePlayer player) {
        super(STR."\{ICON} History");
        this.historyTool = historyTool;
        this.parent = parent;
        this.instance = player.getInstance();
        this.history = player.getHistory();
    }

    @Override
    protected void registerButtons() {
        List<ChangeRecord> future = history.getFuture();
        List<OngoingChange> ongoing = history.getOngoing();
        List<ChangeRecord> past = history.getPast();

        boolean hasFuture = !future.isEmpty();
        boolean hasOngoing = !ongoing.isEmpty();
        boolean hasPast = !past.isEmpty();

        List<ChestButton> buttons = new ArrayList<>();
        for (int i = 0; i < future.size(); ++i) {
            buttons.add(new FutureRecordButton(future.get(i), future.size() - 1 - i));
        }
        buttons.addAll(ongoing.stream().map(OngoingButton::new).toList());
        if (hasPast && (hasFuture || hasOngoing)) {
            buttons.add(new StaticButton(ItemStack.AIR));
        }
        for (int i = past.size() - 1; i >= 0; --i) {
            buttons.add(new PastRecordButtonWithUndo(past.get(i), past.size() - 1 - i));
        }

        if (buttons.isEmpty()) {
            reg(22, new StaticButton(MenuStack.of(Material.CLOCK)
                    .name(MM."<red>No history!")
                    .lore(MM_WRAP."<gray>Consider doing stuff.")));
        } else {
            ChestLayouts.INSIDE.distribute(buttons, this::reg);
        }

        reg(size.bottomCenter() - 2, new UndoButton());
        reg(size.bottomCenter() - 1, new RedoButton());
        reg().goBack(parent);
        reg(size.bottomCenter() + 1, new ToolPickupButton(historyTool));
        reg(size.bottomCenter() + 3, new HistoryInfoButton());
        //reg(size.bottomCenter() + 4, new MergedHistoryProxy(this));
        reg(size.bottomCenter() + 4, new AuditHistoryMenu(this, instance.getWorldStorage().getAuditHistory()));
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<aqua>\{ICON} <title>History";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<cmd>\{HistoryCommand.LABEL}");
        lore.add(Component.empty());

        lore.addAll(MM_WRAP."<gray>Cover up all of your mistakes with /undo and /redo.");
        lore.add(Component.empty());

        lore.add(MM."<cta>Click to view!");

        return MenuStack.of(Material.CLOCK).name(name).lore(lore).build();
    }
}
