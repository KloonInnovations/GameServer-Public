package io.kloon.gameserver.modes.creative.commands.test.schematic;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import net.hollowcube.schem.Rotation;
import net.hollowcube.schem.Schematic;
import net.hollowcube.schem.SchematicReader;
import net.hollowcube.schem.blockpalette.BlockPaletteParser;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.instance.batch.RelativeBlockBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ImportSchematicCommand extends AdminCommand {
    private static final Logger LOG = LoggerFactory.getLogger(ImportSchematicCommand.class);

    public ImportSchematicCommand() {
        super("schemimport");
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                Path filepath = Paths.get("Z:\\Minecraft\\Kloon\\Assets\\mia_creative hub_1.schem");
                if (!Files.exists(filepath)) {
                    player.sendMessage(MM."<red>File not found at: <white>\{filepath.toString()}");
                    return;
                }

                Schematic schematic;
                try {
                    schematic = new SchematicReader().withBlockPaletteParser(new SchemUpgradeParser()).read(filepath);
                } catch (Throwable t) {
                    player.sendMessage(MM."<red>Error!");
                    LOG.error("Error reading schematic!", t);
                    return;
                }

                player.sendMessage("Building...");
                RelativeBlockBatch batch = schematic.build(Rotation.NONE, true);
                player.sendMessage("Pasting...");
                batch.apply(player.getInstance(), 10_000, 0, 10_000, () -> {
                    player.sendMessage(MM."<green>Done!");
                });
            }
        });
    }

    private static class SchemUpgradeParser implements BlockPaletteParser {
        @Override
        public Block parse(String key) {
            if (key.equals("minecraft:grass")) {
                return ArgumentBlockState.staticParse("minecraft:short_grass");
            }

            return ArgumentBlockState.staticParse(key);
        }
    }
}
