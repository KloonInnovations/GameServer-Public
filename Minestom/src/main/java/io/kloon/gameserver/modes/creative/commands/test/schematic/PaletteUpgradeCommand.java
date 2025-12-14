package io.kloon.gameserver.modes.creative.commands.test.schematic;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PaletteUpgradeCommand extends AdminCommand {
    private static final Logger LOG = LoggerFactory.getLogger(PaletteUpgradeCommand.class);

    public PaletteUpgradeCommand() {
        super("upgradepalette");
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                Path filepath = Paths.get("Z:\\Minecraft\\Kloon\\Assets\\mia_creative hub_1.schem");
                if (!Files.exists(filepath)) {
                    player.sendMessage(MM."<red>File not found at: <white>\{filepath.toString()}");
                    return;
                }

                try {
                    Map.Entry<String, CompoundBinaryTag> namedTag = BinaryTagIO.reader().readNamed(filepath, BinaryTagIO.Compression.GZIP);
                    CompoundBinaryTag schematicNbt = namedTag.getValue();
                    CompoundBinaryTag paletteNbt = schematicNbt.getCompound("Palette");
                    paletteNbt.forEach(entry -> {
                        String key = entry.getKey();
                        Block block = parsePaletteBlock(key);
                        if (block == null) {
                            LOG.info(STR."Unknown block: \{key}");
                        }
                    });
                } catch (Throwable t) {
                    LOG.error("Error in upgradepalette", t);
                    player.sendMessage(MM."<red>Error!");
                }
            }
        });
    }

    @Nullable
    private static Block parsePaletteBlock(String key) {
        try {
            return ArgumentBlockState.staticParse(key);
        } catch (ArgumentSyntaxException ex) {
            if (ex.getErrorCode() == ArgumentBlockState.INVALID_BLOCK) {
                return null;
            }
            throw ex;
        }
    }
}
