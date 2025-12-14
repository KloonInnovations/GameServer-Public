package io.kloon.gameserver.modes.creative.patterns.impl.weighted;

import io.kloon.gameserver.modes.creative.commands.patterns.ArgumentPattern;
import io.kloon.gameserver.modes.creative.commands.patterns.PatternParseException;
import io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt.ToolBlockExecutor;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.util.weighted.WeightedEntry;
import io.kloon.gameserver.util.weighted.WeightedTable;

public class WeightedPatternParser {
    public WeightedPattern parse(String input) throws PatternParseException {
        String[] splits = input.split(",");

        WeightedTable<CreativePattern> table = new WeightedTable<>();
        for (String split : splits) {
            WeightedEntry<CreativePattern> entry = parseEntry(split);
            table.put(entry.type(), entry.weight());
        }

        return new WeightedPattern(table);
    }

    public WeightedEntry<CreativePattern> parseEntry(String input) throws PatternParseException {
        if (input.contains("%")) {
            String[] split = input.split("%");
            int weight;
            try {
                weight = Integer.parseInt(split[0]);
            } catch (Throwable t) {
                throw new PatternParseException(input, "Couldn't parse weight");
            }
            CreativePattern pattern = ArgumentPattern.parsePattern(split[1]);
            return new WeightedEntry<>(pattern, weight);
        }
        int weight = 1;
        CreativePattern pattern = ArgumentPattern.parsePattern(input);
        return new WeightedEntry<>(pattern, weight);
    }
}
