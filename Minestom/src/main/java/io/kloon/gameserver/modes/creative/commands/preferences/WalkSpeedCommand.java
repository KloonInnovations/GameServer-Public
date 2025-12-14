package io.kloon.gameserver.modes.creative.commands.preferences;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.preferences.WalkSpeedButton;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WalkSpeedCommand extends Command {
    public static final String LABEL = "walkspeed";

    public static final float DEFAULT_WALK_SPEED = 0.1f;

    public static final float MIN_SPEED = 0.01f;
    public static final float MAX_SPEED = 1f;

    public static final float MIN_SPEED_DISPLAY = FlySpeedCommand.toFmtSpeedNum(MIN_SPEED);
    public static final float MAX_SPEED_DISPLAY = FlySpeedCommand.toFmtSpeedNum(MAX_SPEED);

    public WalkSpeedCommand() {
        super(LABEL);

        ArgumentNumber<Float> speedArg = ArgumentType.Float("speed")
                .min(MIN_SPEED_DISPLAY)
                .max(MAX_SPEED_DISPLAY);
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                float speedInput = context.get(speedArg);
                float walkSpeed = FlySpeedCommand.fromFmtSpeed(speedInput);
                setWalkSpeed(player, walkSpeed);
            }

            @Override
            public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
                return true;
            }
        }, speedArg);
    }

    public static void setWalkSpeed(CreativePlayer player, float speed) {
        float speedBefore = (float) player.getAttributeValue(Attribute.MOVEMENT_SPEED);
        player.getCreativeStorage().setWalkSpeed(speed);
        player.applyWalkSpeedFromStorage();

        String fmtBefore = FlySpeedCommand.toFmtSpeed(speedBefore);
        String fmtAfter = FlySpeedCommand.toFmtSpeed(speed);

        double pitch = 0.7 + (speed / MAX_SPEED) * 1.3;
        player.msg().send(MsgCat.PREFERENCE,
                NamedTextColor.GOLD, "WALK SPEED", MM."<gray>Adjusted from \{fmtBefore} to <gold>\{fmtAfter} \{WalkSpeedButton.ICON}<gray>!",
                SoundEvent.ENTITY_PIGLIN_BRUTE_STEP, pitch, 0.7);
    }
}
