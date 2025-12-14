package io.kloon.gameserver.modes.creative.menu.history.audit;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.async.AsyncPlayerButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.util.formatting.TimeFmt;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class AuditRecordButton extends AsyncPlayerButton<AuditRecordButton.Data> {
    private final AuditRecord record;

    public AuditRecordButton(ChestMenu menuOfButton, int slot, AuditRecord record) {
        super(menuOfButton, slot);
        this.record = record;
    }

    @Override
    public CompletableFuture<Data> fetchData(Player player) {
        return Kgs.getCaches().monikers().getByAccountId(record.author()).thenApply(Data::new);
    }

    @Override
    public void handleClickWithData(Player player, ButtonClick click, Data data) {

    }

    @Override
    public ItemStack renderWithData(Player player, Data data) {
        ChangeMeta meta = record.meta();

        Component name = MM."\{meta.changeTitleMM()}";

        Lore lore = new Lore();
        lore.add(MM."<gray>Player: \{data.moniker.getDisplayMM()}");
        lore.addEmpty();
        lore.wrap(meta.chatText());
        lore.addEmpty();

        lore.add(MM."<gray>Tool: <white>\{meta.tool().getDisplayName()}");
        lore.add(MM."<gray>Happened: <green>\{TimeFmt.naturalTime(record.timestamp())}");

        Material icon = meta.tool().getMaterial();
        return MenuStack.of(icon, name, lore);
    }

    public record Data(KloonMoniker moniker) {}
}
