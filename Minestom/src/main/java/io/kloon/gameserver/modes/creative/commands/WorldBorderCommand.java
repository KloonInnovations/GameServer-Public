package io.kloon.gameserver.modes.creative.commands;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.CreativePreferencesMenu;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.PlayerStorageToggleButton;
import io.kloon.gameserver.modes.creative.storage.playerdata.CreativePlayerStorage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.coordinate.BlockVec;
import org.jetbrains.annotations.NotNull;

public class WorldBorderCommand extends Command {
    public static final String LABEL = "worldborder";

    public WorldBorderCommand() {
        super(LABEL);
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativePlayerStorage storage = player.getCreativeStorage();
                boolean render = !storage.isRenderingWorldBorder();
                storage.setRenderingWorldBorder(render);
                applyWorldBorder(player);

                PlayerStorageToggleButton.sendToggleChangeFx(player, CreativePreferencesMenu.RENDER_WORLD_BORDER, render);
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        });
    }

    public static void applyWorldBorder(CreativePlayer player) {
        boolean render = player.getCreativeStorage().isRenderingWorldBorder();
        CreativeInstance instance = player.getInstance();
        if (render) {
            player.sendWorldBorder(instance.getChunkLoader().getBoundsAsBorder());
        } else {
            BlockVec center = instance.getWorldCenter();
            player.sendWorldBorder(center, Double.MAX_VALUE / 2);
        }
    }
}
