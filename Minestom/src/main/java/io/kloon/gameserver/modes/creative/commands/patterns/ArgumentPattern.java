package io.kloon.gameserver.modes.creative.commands.patterns;

import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.WeightedPatternParser;
import net.minestom.server.command.ArgumentParserType;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.NetworkBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArgumentPattern extends Argument<CreativePattern> {
    private boolean string;

    private ArgumentPattern(@NotNull String id) {
        super(id);
    }

    public ArgumentPattern string() {
        this.string = true;
        return this;
    }

    @Override
    public @NotNull CreativePattern parse(@NotNull CommandSender sender, @NotNull String input) throws ArgumentSyntaxException {
        return parsePattern(input);
    }

    @Override
    public ArgumentParserType parser() {
        return string ? ArgumentParserType.STRING : ArgumentParserType.BLOCK_STATE;
    }

    @Override
    public byte @Nullable [] nodeProperties() {
        if (string) {
            return NetworkBuffer.makeArray(packetWriter -> {
                packetWriter.write(NetworkBuffer.VAR_INT, 1); // Quotable phrase
            });
        }
        return super.nodeProperties();
    }

    @Override
    public String toString() {
        return String.format("Pattern<%s>", getId());
    }

    public static ArgumentPattern create(String id) {
        return new ArgumentPattern(id);
    }

    public static final WeightedPatternParser WEIGHTED_PARSER = new WeightedPatternParser();

    public static CreativePattern parsePattern(String input) throws PatternParseException {
        if (input.contains(",")) {
            return WEIGHTED_PARSER.parse(input);
        }

        Block block = parseBlock(input);
        if (block == null) {
            throw new PatternParseException(input, "Unknown Minecraft block!");
        }
        return new SingleBlockPattern(block);
    }

    @Nullable
    public static Block parseBlock(String input) throws PatternParseException {
        if (input.contains(":")) {
            input = input.replace("minecraft:", "");
        }
        try {
            return ArgumentBlockState.staticParse(input);
        } catch (Throwable t) {
            throw new PatternParseException(input, t.getMessage());
        }
    }
}
