package io.kloon.gameserver.modes.creative.patterns.impl.weighted;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.PatternType;
import io.kloon.gameserver.modes.creative.patterns.RecursivePattern;
import io.kloon.gameserver.util.weighted.WeightedEntry;
import io.kloon.gameserver.util.weighted.WeightedTable;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WeightedPattern extends CreativePattern implements RecursivePattern {
    private final WeightedTable<CreativePattern> table;
    private @Nullable CreativePattern parent;

    public static final int MAX_ENTRIES = 27;

    public static final TextColor PERCENT_COLOR = TextColor.color(104, 119, 105);
    public static final String PERCENT_HEX = PERCENT_COLOR.asHexString();

    public WeightedPattern(WeightedTable<CreativePattern> table) {
        super(PatternType.WEIGHTED_RANDOM);
        this.table = table;
        table.getTypes().forEach(child -> {
            if (child instanceof RecursivePattern recursive) {
                recursive.setParent(this);
            }
        });
    }

    public void put(WeightedEntry<CreativePattern> entry) {
        put(entry.type(), entry.weight());
    }

    public void put(CreativePattern pattern, int weight) {
        table.put(pattern, weight);
        if (pattern instanceof RecursivePattern rec) {
            rec.setParent(this);
        }
    }

    public void remove(CreativePattern pattern) {
        table.remove(pattern);
        if (pattern instanceof RecursivePattern rec) {
            rec.setParent(null);
        }
    }

    public long getTotalWeight() {
        return table.getTotalWeight();
    }

    public void setParent(@Nullable CreativePattern parent) {
        this.parent = parent;
    }

    public @Nullable CreativePattern parent() {
        return parent;
    }

    public List<WeightedEntry<CreativePattern>> getChildrenAndWeights() {
        return table.getTypeAndWeights();
    }

    @Override
    public Set<CreativePattern> children() {
        return table.getTypes();
    }

    public int size() {
        return children().size();
    }

    @Override
    public String labelMM() {
        long totalWeight = table.getTotalWeight();
        List<WeightedEntry<CreativePattern>> entries = table.getTypeAndWeights();
        int limit = 3;
        List<String> labels = new ArrayList<>(limit);
        for (int i = 0; i < Math.min(entries.size(), limit); ++i) {
            WeightedEntry<CreativePattern> entry = entries.get(i);
            String entryLabel = entry.type().labelMM();
            if (entry.type() instanceof WeightedPattern) {
                entryLabel = STR."<dark_green>Weighted";
            }

            long percent = Math.round((double) entry.weight() / totalWeight * 100);
            String label = STR."\{percent}%\{entryLabel}";
            labels.add(label);
        }
        String joined = String.join(STR."<\{PERCENT_HEX}>,", labels);
        String append = entries.size() > limit ? STR."<\{PERCENT_HEX}>..." : "";
        return STR."<\{PERCENT_HEX}>\{joined}\{append}";
    }

    @Override
    public Lore lore() {
        long totalWeight = table.getTotalWeight();
        Lore lore = new Lore();
        int limit = 4;
        List<WeightedEntry<CreativePattern>> entries = table.getTypeAndWeights();
        entries.stream().limit(limit).forEach(entry -> {
            long percent = Math.round((double) entry.weight() / totalWeight * 100);
            lore.add(MM."<\{PERCENT_HEX}>\{percent}% \{entry.type().labelMM()}");
        });
        if (entries.size() > limit) {
            int over = entries.size();
            lore.add(MM."<\{PERCENT_HEX}>And \{over} more...");
        }
        return lore;
    }

    @Override
    public ItemBuilder2 icon() {
        List<WeightedEntry<CreativePattern>> entries = table.getTypeAndWeights();
        if (!entries.isEmpty()) {
            return entries.getFirst().type().icon();
        }
        return getType().icon();
    }

    @Override
    public CreativePattern compute(Instance instance, Point blockPos) {
        CreativePattern rolled = table.roll();
        return rolled.compute(instance, blockPos);
    }

    private WeightedTable<CreativePattern> copyTable() {
        WeightedTable<CreativePattern> tableCopy = new WeightedTable<>();
        for (WeightedEntry<CreativePattern> entry : table.getTypeAndWeights()) {
            CreativePattern patternCopy = entry.type().copy();
            tableCopy.put(patternCopy, entry.weight());
        }
        return tableCopy;
    }

    @Override
    public WeightedPattern copy() {
        WeightedTable<CreativePattern> tableCopy = copyTable();
        WeightedPattern pattern = new WeightedPattern(tableCopy);
        pattern.parent = parent;
        return pattern;
    }

    @Override
    public boolean canBePickedUp() {
        return !table.getTypeAndWeights().isEmpty();
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<WeightedPattern> {
        @Override
        public void encode(WeightedPattern obj, MinecraftOutputStream out) throws IOException {
            Set<CreativePattern> patterns = obj.table.getTypes();
            out.writeVarInt(patterns.size());
            for (CreativePattern pattern : patterns) {
                out.write(pattern, CreativePattern.CODEC);
                int weight = obj.table.getWeight(pattern);
                out.writeVarInt(weight);
            }
        }

        @Override
        public WeightedPattern decode(MinecraftInputStream in) throws IOException {
            WeightedTable<CreativePattern> table = new WeightedTable<>();

            int size = in.readVarInt();
            for (int i = 0; i < size; ++i) {
                CreativePattern pattern = in.read(CreativePattern.CODEC);
                int weight = in.readVarInt();
                table.put(pattern, weight);
            }

            return new WeightedPattern(table);
        }
    }
}
