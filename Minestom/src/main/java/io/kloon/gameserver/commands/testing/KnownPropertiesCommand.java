package io.kloon.gameserver.commands.testing;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class KnownPropertiesCommand extends AdminCommand {
    private static final Logger LOG = LoggerFactory.getLogger(KnownPropertiesCommand.class);

    public KnownPropertiesCommand() {
        super("knownproperties");
        setDefaultExecutor((sender, context) -> {
            Multiset<String> properties = HashMultiset.create();

            for (Block block : Block.values()) {
                Map<String, Collection<String>> options = block.propertyOptions();
                properties.addAll(options.keySet());
            }

            properties.entrySet().stream()
                    .sorted(Comparator.comparingInt(e -> -e.getCount()))
                    .forEach(e -> {
                        LOG.info(e.getCount() + " " + e.getElement());
                    });

            sender.sendMessage(MM."<green>Sent to console!");
        });

        ArgumentString propertyArg = ArgumentType.String("property");
        addSyntax((sender, context) -> {
            String propertyLookup = context.get(propertyArg);

            Set<Block> blocksWithProperty = new HashSet<>();
            for (Block block : Block.values()) {
                Map<String, Collection<String>> options = block.propertyOptions();
                if (options.containsKey(propertyLookup)) {
                    blocksWithProperty.add(block);
                }
            }

            LOG.info(STR."With \{propertyLookup}:");
            blocksWithProperty.forEach(ageBlock -> {
                LOG.info(ageBlock.name());
            });

            sender.sendMessage(MM."<green>Sent to console!");
        }, propertyArg);
    }
}
