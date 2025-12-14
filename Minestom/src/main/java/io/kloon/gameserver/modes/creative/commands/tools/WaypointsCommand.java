package io.kloon.gameserver.modes.creative.commands.tools;

import io.kloon.gameserver.minestom.utils.PointFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointsStorage;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.WaypointMenu;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.WaypointsManagementMenu;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WaypointsCommand extends Command {
    public static final String LABEL = "waypoints";
    public static final String LABEL_SHORT = "wp";

    public WaypointsCommand() {
        super(LABEL, "waypoint", LABEL_SHORT);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                openWaypointsMenu(player);
            }
        });

        ArgumentStringArray nameArg = ArgumentType.StringArray("name");
        nameArg.setSuggestionCallback((sender, context, suggestion) -> {
            if (!(sender instanceof CreativePlayer player)) return;
            WaypointsStorage waypoints = player.getInstance().getWorldStorage().getWaypoints();
            String input = suggestion.getInput().toLowerCase().replace("wp ", "").trim();
            waypoints.getList().forEach(waypoint -> {
                String nameLower = waypoint.getName().toLowerCase();
                if (!nameLower.startsWith(input)) return;

                Pos pos = waypoint.getPosition();
                String color = waypoint.getColor().getTextColor().asHexString();
                suggestion.addEntry(new SuggestionEntry(waypoint.getName(), MM."<\{color}>\{PointFmt.fmt10k(pos)}"));
            });
        });
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                WaypointsStorage waypoints = player.getInstance().getWorldStorage().getWaypoints();
                String inputName = String.join(" ", context.get(nameArg));
                WaypointStorage waypoint = waypoints.getByNameIgnoreCase(inputName);
                if (waypoint == null) {
                    player.playSound(SoundEvent.BLOCK_BEACON_DEACTIVATE, 2);
                    player.sendPit(NamedTextColor.RED, "NOT FOUND", MM."<gray>No waypoint found by that name!");
                    player.sendMessage(MM."<yellow>Click to open the waypoints management menu!"
                            .clickEvent(ClickEvent.runCommand("/" + LABEL))
                            .hoverEvent(MM."<green>Click!"));
                    return;
                }

                waypoint.openMenu(player);
            }
        }, nameArg);
    }

    public static void openWaypointsMenu(CreativePlayer player) {
        CreativeMainMenu mainMenu = new CreativeMainMenu(player);
        new WaypointsManagementMenu(mainMenu, player).display(player);
    }
}
