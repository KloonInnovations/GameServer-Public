package io.kloon.gameserver.ux.sidebar;

import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.gameserver.modes.creative.ux.sidebar.title.RainbowRotation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Sidebar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static net.minestom.server.scoreboard.Sidebar.*;

public abstract class KloonSidebar {
    private static final Logger LOG = LoggerFactory.getLogger(KloonSidebar.class);
    private static final int MAX_LINES = 15;

    private final Player player;

    private final Sidebar sidebar;

    private final List<ScoreboardLine> sbLines = new ArrayList<>();
    private List<Component> linesBefore = new ArrayList<>();

    private final RainbowRotation stainRot = new RainbowRotation();

    public KloonSidebar(Player player) {
        this.player = player;
        this.sidebar = new Sidebar(MM."<#FF266E><b>KLOON.IO</b></#FF266E> <#FF3EA5>\uD83C\uDF0C <#FF266E><b>CREATIVE");
    }

    public final void tick() {
        if (!sidebar.isViewer(player)) {
            sidebar.addViewer(player);
        }

        tickTitle();

        if (GlobalMinestomTicker.getTick() % 5 == 0) {
            tickSidebarLines();
        }
    }

    private void tickTitle() {
        Component title;
        try {
            title = renderTitle();
        } catch (Throwable t) {
            LOG.error("Error generating title");
            title = MM."<dark_red><b>TITLE ERROR";
        }

        sidebar.setTitle(title);
    }

    public Component renderTitle() {
        MiniMessage mm = MiniMessage.miniMessage();
        String stain = stainRot.createPart("\uD83C\uDF0C"); // 🌌
        String titleStr = STR."\{stain} <#FF266E><b>KLOON.IO</b></#FF266E>";
        return mm.deserialize(titleStr);
    }

    private void tickSidebarLines() {
        List<Component> lines;
        try {
            lines = renderLines();
        } catch (Throwable t) {
            LOG.error("Error rendering sidebar lines!", t);
            lines = MM_WRAP."<red>Error rendering the sidebar!";
        }

        if (lines.size() > MAX_LINES) {
            lines = lines.subList(0, MAX_LINES);
        }

        int updateUpTo = Math.min(lines.size(), sbLines.size());
        if (lines.size() > sbLines.size()) {
            int missing = lines.size() - sbLines.size();
            for (int i = 0; i < missing; ++i) {
                int index = sbLines.size();
                Component content = lines.get(index);
                String lineId = STR."side_\{index}";
                ScoreboardLine sbLine = new ScoreboardLine(lineId, content, MAX_LINES - index, NumberFormat.blank());
                sbLines.add(sbLine);
                sidebar.createLine(sbLine);
            }
        } else if (lines.size() < sbLines.size()) {
            List<ScoreboardLine> extra = sbLines.subList(lines.size(), sbLines.size());
            extra.forEach(sbLine -> sidebar.removeLine(sbLine.getId()));
            extra.clear();
        }

        for (int i = 0; i < updateUpTo; ++i) {
            ScoreboardLine sbLine = sbLines.get(i);

            Component lineBefore = i >= linesBefore.size() ? null : linesBefore.get(i);
            Component lineNow = lines.get(i);

            if (!Objects.equals(lineBefore, lineNow)) {
                sidebar.updateLineContent(sbLine.getId(), lineNow);
            }
        }

        linesBefore = lines;
    }

    public abstract List<Component> renderLines();
}
