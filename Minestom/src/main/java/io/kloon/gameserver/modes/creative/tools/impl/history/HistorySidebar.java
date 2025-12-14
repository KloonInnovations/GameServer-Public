package io.kloon.gameserver.modes.creative.tools.impl.history;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.ChangeRecord;
import io.kloon.gameserver.modes.creative.history.History;
import io.kloon.gameserver.modes.creative.tools.ToolSidebar;
import io.kloon.gameserver.util.formatting.TimeFmt;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.tools.impl.history.HistoryTool.*;

public class HistorySidebar implements ToolSidebar<Settings, Preferences> {
    @Override
    public Lore generate(CreativePlayer player, Settings settings, Preferences preferences) {
        Lore lore = new Lore();
        History history = player.getHistory();

        List<ChangeRecord> future = history.getFuture();
        int futureLimit = 3;
        if (!future.isEmpty()) {
            lore.add(MM."<white>Future");
            lore.add(renderRecords(future, futureLimit));
            lore.addEmpty();
        }

        lore.add(MM."<white>Past");
        List<ChangeRecord> past = history.getPast().reversed();
        int pastLimit = 9 - Math.min(futureLimit, future.size());
        lore.add(renderRecords(past, pastLimit));

        return lore;
    }

    private Lore renderRecords(List<ChangeRecord> records, int limit) {
        Lore lore = new Lore();
        if (records.isEmpty()) {
            lore.add(MM."<red>Literally nothing!");
        } else {
            List<String> timeFmts = records.stream().limit(limit).map(r -> TimeFmt.singleLetterAgo(r.endTimestamp())).toList();
            boolean pad = timeFmts.stream().anyMatch(fmt -> fmt.length() > 2);
            for (int i = 0; i < Math.min(records.size(), limit); ++i) {
                ChangeRecord change = records.get(i);
                String timeFmt = TimeFmt.singleLetterAgo(change.endTimestamp());
                if (pad && timeFmt.length() == 2) {
                    timeFmt = "..." + timeFmt;
                }
                String changeFmt = change.meta().changeTitleMM();
                lore.add(MM."<dark_gray>\{timeFmt} <white>\{changeFmt}");
            }
        }
        return lore;
    }
}
