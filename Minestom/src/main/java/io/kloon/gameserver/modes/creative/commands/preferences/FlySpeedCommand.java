package io.kloon.gameserver.modes.creative.commands.preferences;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.preferences.FlySpeedButton;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class FlySpeedCommand extends Command {
    public static final String LABEL = "flyspeed";

    public static final float DEFAULT_FLY_SPEED = 0.05f;

    public static final float MIN_SPEED = 0.005f;
    public static final float MAX_SPEED = 0.5f;

    public static final float MIN_SPEED_DISPLAY = toFmtSpeedNum(MIN_SPEED);
    public static final float MAX_SPEED_DISPLAY = toFmtSpeedNum(MAX_SPEED);

    public FlySpeedCommand() {
        super(LABEL);

        ArgumentNumber<Float> speedArg = ArgumentType.Float("speed")
                .min(MIN_SPEED_DISPLAY)
                .max(MAX_SPEED_DISPLAY);
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                float speedInput = context.get(speedArg);
                float flySpeed = fromFmtSpeed(speedInput);
                setFlySpeed(player, flySpeed);
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        }, speedArg);
    }

    public static void setFlySpeed(CreativePlayer player, float speed) {
        float speedBefore = player.getFlyingSpeed();
        player.setFlyingSpeed(speed);

        double pitch = 0.7 + (speed / MAX_SPEED) * 1.3;
        player.msg().send(MsgCat.PREFERENCE,
                NamedTextColor.YELLOW, "FLY SPEED", MM."<gray>Adjusted from \{toFmtSpeed(speedBefore)} to <yellow>\{toFmtSpeed(speed)} \{FlySpeedButton.ICON}<gray>!",
                SoundEvent.ENTITY_PARROT_FLY, Pitch.base(pitch), 0.7);
    }

    public static String toFmtSpeed(float speed) {
        return NumberFmt.ONE_DECIMAL.format(toFmtSpeedNum(speed));
    }

    public static float toFmtSpeedNum(float speed) {
        return speed * 100;
    }

    public static float fromFmtSpeed(float speed) {
        return speed / 100;
    }
}
