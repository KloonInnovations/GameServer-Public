package io.kloon.gameserver.modes.creative.ux.sidebar;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.components.ComponentWrapper;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativeMode;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.ChangeRecord;
import io.kloon.gameserver.modes.creative.history.History;
import io.kloon.gameserver.modes.creative.selection.CuboidSelection;
import io.kloon.gameserver.modes.creative.selection.NoCuboidSelection;
import io.kloon.gameserver.modes.creative.selection.OneCuboidSelection;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.storage.playerdata.SelectionColors;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClip;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.selection.PastingSelection;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.gameserver.util.formatting.TimeFmt;
import io.kloon.gameserver.ux.sidebar.KloonSidebar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.minestom.utils.PointFmt.fmt10k;

public class CreativeSidebar extends KloonSidebar {
    private static final Logger LOG = LoggerFactory.getLogger(CreativeSidebar.class);

    private final CreativePlayer player;

    public CreativeSidebar(CreativePlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    public List<Component> renderLines() {
        List<Component> lore = new ArrayList<>();

        CreativeInstance instance = player.getInstance();

        lore.add(MM."<dark_gray>\{instance.getCuteName()}");
        lore.add(Component.empty());
        int emptySize = lore.size();

        List<List<Component>> sections = new ArrayList<>();
        Consumer<List<Component>> addSection = section -> {
            if (section.isEmpty()) return;
            sections.add(section);
        };

        ItemStack inHand = player.getItemInMainHand();
        CreativeMode creative = player.getCreative();

        CreativeTool tool = creative.getToolsListener().get(inHand);
        Lore toolSidebar = tool == null ? null : tool.generateSidebar(player, inHand);

        if (toolSidebar != null) {
            addSection.accept(toolSidebar.asList());
        } else {
            addSection.accept(renderSelection());
            addSection.accept(renderPastingSelection().asList());
        }

        for (int i = 0; i < sections.size(); ++i) {
            lore.addAll(sections.get(i));

            boolean lastSection = i == sections.size() - 1;
            if (!lastSection) {
                lore.add(Component.empty());
            }
        }

        if (lore.size() == emptySize) {
            lore.addAll(ComponentWrapper.wrap(MM."<white>This is a sidebar, until proven otherwise.", 18));
        }

        return lore;
    }

    private List<Component> renderSelection() {
        List<Component> lore = new ArrayList<>();

        SelectionColors selectionColors = player.getCreativeStorage().getSelectionColors();
        TextColor color1 = TextColor.color(selectionColors.getOneSelection());
        TextColor color2 = TextColor.color(selectionColors.getFullSelection());

        CuboidSelection selection = player.getSelection();
        if (selection instanceof NoCuboidSelection) {
            if (player.isHoldingTool(CreativeToolType.SELECTION)) {
                lore.add(MM."<white>\{CreativeMode.SELECTION_ICON} Selection");
                lore.add(MM."<dark_gray>⇨ <#FF266E>Click a block!");
            }
        } else if (selection instanceof OneCuboidSelection s) {
            lore.add(MM."<white>\{CreativeMode.SELECTION_ICON} Selection");
            lore.add(MM."<dark_gray>1 <\{color1.asHexString()}>\{fmt10k(s.getPos1())}");
            lore.add(MM."<dark_gray>⇨ <#FF266E>Click another block!");
        } else if (selection instanceof TwoCuboidSelection s) {
            BoundingBox cuboid = s.getCuboid();
            long volume = BoundingBoxUtils.volumeRounded(cuboid);
            String volumeFmt = NumberFormat.getInstance().format(volume);
            String blocksFmt = volumeFmt + (volume == 1 ? " block" : " blocks");
            lore.add(MM."<white>\{CreativeMode.SELECTION_ICON} Selection <gray>\{blocksFmt}");
            lore.add(MM."<dark_gray>1 <\{color1.asHexString()}>\{fmt10k(s.getPos1())}");
            lore.add(MM."<dark_gray>2 <\{color2.asHexString()}>\{fmt10k(s.getPos2())}");
            lore.add(MM."<gray>\{(int) cuboid.width()}x\{(int) cuboid.height()}x\{(int) cuboid.depth()} cuboid");
        }

        return lore;
    }

    private Lore renderPastingSelection() {
        Lore lore = new Lore();
        if (!(player.getPasteSelection() instanceof PastingSelection pasting)) {
            return lore;
        }

        WorldClip clip = pasting.getClip();
        int clipIndex = player.getClipboard().getClipIndex(clip);
        if (clip == null || clipIndex < 0) {
            lore.add(MM."<red>Unknown clip!");
            return lore;
        }

        int secondsLeft = (int) Math.ceil(pasting.getTicksUntilDisappear() / 20.0);

        lore.add(MM."<white>Pasting #\{clipIndex + 1} <dark_gray>\{secondsLeft}s");

        BoundingBox bb = clip.volume().toCuboid();

        long volume = BoundingBoxUtils.volumeRounded(bb);
        String volumeFmt = NumberFmt.NO_DECIMAL.format(volume);
        lore.add(MM."<gray>\{(int) bb.width()}x\{(int) bb.height()}x\{(int) bb.depth()}, \{volumeFmt} blocks");

        return lore;
    }
}
