package io.kloon.gameserver.modes.creative.tools.impl.teleport.commands;

import io.kloon.gameserver.minestom.commands.PlayerListSuggestionCallback;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.PointFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.TeleportChange;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.TeleportTool;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.infra.util.cooldown.CooldownMap;
import io.kloon.infra.util.cooldown.impl.TimeCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class TeleportCommand extends ToolOperationCommand<TeleportTool> {
    public static final String LABEL = "tp";
    public static final CooldownMap<SenderAndRecipient, TimeCooldown> MSG_CD = new CooldownMap<>(() -> new TimeCooldown(4, TimeUnit.SECONDS));

    public TeleportCommand(TeleportTool tool) {
        super(tool, LABEL, true);

        ArgumentFloat xArg = ArgumentType.Float("x");
        ArgumentFloat yArg = ArgumentType.Float("y");
        ArgumentFloat zArg = ArgumentType.Float("z");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                float x = context.get(xArg);
                float y = context.get(yArg);
                float z = context.get(zArg);

                Pos posBefore = player.getPosition();

                Pos tpPos = player.getPosition().withCoord(x, y, z);
                player.teleport(tpPos);

                SentMessage msg = player.msg().send(MsgCat.TOOL,
                        NamedTextColor.DARK_PURPLE, "TELEPORTED!", MM."<gray>to <green>\{PointFmt.fmt10k(tpPos)}<gray>!",
                        SoundEvent.ENTITY_ENDERMAN_TELEPORT, Pitch.base(1.3).addRand(0.25));

                player.addToHistory(tool.getType(), "<dark_purple>Command teleport!",
                        msg, new TeleportChange(posBefore, player.isFlying(), player));
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        }, xArg, yArg, zArg);

        ArgumentString playerArg = ArgumentType.String("player");
        playerArg.setSuggestionCallback(new PlayerListSuggestionCallback());
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                String nameInput = context.get(playerArg);
                Player targetPlayer = player.getInstance().getPlayers().stream()
                        .filter(p -> p.getUsername().equalsIgnoreCase(nameInput))
                        .findFirst().orElse(null);
                if (!(targetPlayer instanceof CreativePlayer target)) {
                    player.sendPit(NamedTextColor.RED, "WHO?", MM."<gray>Couldn't find a player by that name on your world!");
                    return;
                }

                if (target == player) {
                    player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 1.3);
                    player.sendPit(NamedTextColor.RED, "UH?", MM."<gray>You know.. if you don't move, it's like you're already teleporting to yourself... all the time.");
                    return;
                }

                Pos posBefore = player.getPosition();
                Pos tpPos = target.getPosition();

                player.teleport(tpPos);
                SentMessage sentMsg = player.msg().send(MsgCat.TOOL,
                        NamedTextColor.DARK_PURPLE, "TELEPORTED!", MM."<gray>to <green>\{target.getDisplayMM()}<gray> at <green>\{PointFmt.fmt10k(tpPos)}<gray>!",
                        SoundEvent.ENTITY_ENDERMAN_TELEPORT, Pitch.base(1.3).addRand(0.25));

                player.addToHistory(tool.getType(), "<dark_purple>Command teleport!",
                        sentMsg, new TeleportChange(posBefore, player.isFlying(), player));

                if (MSG_CD.get(new SenderAndRecipient(player.getUuid(), target.getUuid())).cooldownIfPossible()) {
                    target.sendPit(NamedTextColor.DARK_PURPLE, "TP!", MM."\{player.getDisplayMM()} <gray>has <green>/teleport<gray>ed to you!");
                }
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        }, playerArg);
    }

    public record SenderAndRecipient(UUID sender, UUID recipient) {}

    @Override
    public List<ToolOperationUsage> getUsages() {
        return List.of(
                new ToolOperationUsage("<x> <y> <z>", "Teleport to specific coordinates."),
                new ToolOperationUsage("<username>", "Teleport to a player.")
        );
    }
}
