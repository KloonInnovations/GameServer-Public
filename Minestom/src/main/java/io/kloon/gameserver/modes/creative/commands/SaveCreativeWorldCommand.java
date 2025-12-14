package io.kloon.gameserver.modes.creative.commands;

import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTimeCooldownMap;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SaveCreativeWorldCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(SaveCreativeWorldCommand.class);

    public static final String LABEL = "savecreativeworld";

    private final PlayerTimeCooldownMap cooldown = new PlayerTimeCooldownMap(25, TimeUnit.SECONDS);

    public SaveCreativeWorldCommand() {
        super(LABEL);
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativeInstance instance = player.getInstance();

                if (!cooldown.get(instance.getUniqueId()).cooldownIfPossible()) {
                    player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 1f);
                    player.sendMessage(MM."<red>Sorry, but saving is on cooldown!");
                    return;
                }

                player.sendMessage(MM."<gray>Saving the world...");
                instance.saveInstance(WorldSave.Reason.COMMAND).whenComplete((_, t) -> {
                    if (t != null) {
                        player.sendMessage(MM."<red>There was an error saving the world!");
                        LOG.error("Error saving instance", t);
                        return;
                    }

                    player.sendPit(NamedTextColor.GREEN, "SAVED!", MM."<gray>World has been saved! That makes you a hero!");
                });
            }
        });
    }
}
