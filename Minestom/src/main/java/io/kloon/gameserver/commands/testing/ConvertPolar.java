package io.kloon.gameserver.commands.testing;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.hollowcube.polar.PolarFormat;
import net.hollowcube.polar.anvil.AnvilPolar;
import net.hollowcube.polar.model.PolarWorld;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ConvertPolar extends AdminCommand {
    private static final Logger LOG = LoggerFactory.getLogger(ConvertPolar.class);

    public ConvertPolar() {
        super("convertpolar");

        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                try {
                    long startMs = System.currentTimeMillis();
                    PolarWorld polarWorld = AnvilPolar.anvilToPolar(Paths.get("Kloon"));
                    byte[] bytes = PolarFormat.WRITER.write(polarWorld);

                    Path outputPath = Paths.get("world.polar");
                    Files.write(outputPath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                    long elapsedMs = System.currentTimeMillis() - startMs;
                    String bytesFmt = NumberFmt.NO_DECIMAL.format(bytes.length);
                    String elapsedFmt = NumberFmt.NO_DECIMAL.format(elapsedMs);
                    player.sendMessage(MM."<green>Wrote <white>\{bytesFmt} <green>bytes in <white>\{elapsedFmt}<green>ms !");
                } catch (Throwable t) {
                    player.sendMessage(MM."<red>Error!");
                    LOG.error("Error convertpolar", t);
                }
            }
        });
    }
}
