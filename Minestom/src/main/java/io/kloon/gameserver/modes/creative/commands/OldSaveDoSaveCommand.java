package io.kloon.gameserver.modes.creative.commands;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class OldSaveDoSaveCommand extends Command {
    public static final String LABEL = "oldsavedosave";

    public OldSaveDoSaveCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativeInstance instance = player.getInstance();
                boolean oldSaveDontSave = instance.isOldSaveDontSave();
                if (!oldSaveDontSave) {
                    player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 0.8f);
                    player.sendPit(NamedTextColor.RED, "HEY!", MM."<gray>Saving is already enabled!");
                    return;
                }

                instance.withOldSaveDontSave(false);
            }
        });
    }
}
