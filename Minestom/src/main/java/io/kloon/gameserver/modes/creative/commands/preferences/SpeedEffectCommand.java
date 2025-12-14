package io.kloon.gameserver.modes.creative.commands.preferences;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SpeedEffectCommand extends Command {
    public static final String LABEL = "speed";

    public static final int MAX = 9;

    public SpeedEffectCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                setSpeedEffect(player, -1);
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        });

        ArgumentNumber<Integer> amplifierArg = ArgumentType.Integer("amplifier").max(MAX);
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                int amplifierInput = context.get(amplifierArg);
                setSpeedEffect(player, amplifierInput);
            }
        }, amplifierArg);
    }

    public static void setSpeedEffect(CreativePlayer player, int amplifier) {
        player.getCreativeStorage().setSpeedEnchant(amplifier);
        player.applyWalkSpeedFromStorage();
        if (amplifier < 0) {
            player.msg().send(MsgCat.PREFERENCE,
                    NamedTextColor.DARK_BLUE, "THE SLOW", MM."<gray>Removed speed effect!",
                    SoundEvent.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2f);
        } else {
            double pitch = 0.9 + (amplifier / 8.0);
            player.msg().send(MsgCat.PREFERENCE,
                    NamedTextColor.BLUE, "THE FAST", MM."<gray>Set speed potion effect amplifier to <aqua>\{amplifier}<gray>!",
                    SoundEvent.ENTITY_PUFFER_FISH_BLOW_OUT, pitch);
        }
    }
}
