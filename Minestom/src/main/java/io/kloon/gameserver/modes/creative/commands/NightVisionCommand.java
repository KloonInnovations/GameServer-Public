package io.kloon.gameserver.modes.creative.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.CreativePreferencesMenu;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggleButton;
import io.kloon.gameserver.modes.creative.storage.playerdata.CreativePlayerStorage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public class NightVisionCommand extends Command {
    public static final String SHORT_LABEL = "nv";

    public NightVisionCommand() {
        super("nightvision", SHORT_LABEL);
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativePlayerStorage storage = player.getCreativeStorage();
                boolean nightVision = !storage.hasNightVision();
                storage.setNightVision(nightVision);
                applyNightVision(player);

                PlayerStorageToggleButton.sendToggleChangeFx(player, CreativePreferencesMenu.NIGHT_VISION_TOGGLE, nightVision);
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        });
    }

    public static void applyNightVision(CreativePlayer player) {
        boolean nightVision = player.getCreativeStorage().hasNightVision();
        if (nightVision) {
            player.addEffect(new Potion(PotionEffect.NIGHT_VISION, (byte) 0, -1));
        } else {
            player.removeEffect(PotionEffect.NIGHT_VISION);
        }
    }
}
