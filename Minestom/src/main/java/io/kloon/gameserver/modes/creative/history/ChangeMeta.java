package io.kloon.gameserver.modes.creative.history;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import net.kyori.adventure.text.Component;
import net.minestom.server.sound.SoundEvent;

import java.io.IOException;

public record ChangeMeta(
        CreativeToolType tool,
        String changeTitleMM,
        Component chatText,
        SoundEvent sound,
        double soundPitch
) {
    public CoolSound coolSound() {
        return new CoolSound(sound, soundPitch);
    }

    public static final Codec CODEC = new Codec();

    public static final class Codec implements MinecraftCodec<ChangeMeta> {
        @Override
        public void encode(ChangeMeta obj, MinecraftOutputStream out) throws IOException {
            out.writeString(obj.tool.getDbKey());
            out.writeString(obj.changeTitleMM);
            out.writeComponent(obj.chatText);
            out.writeString(obj.sound.name());
            out.writeDouble(obj.soundPitch);
        }

        @Override
        public ChangeMeta decode(MinecraftInputStream in) throws IOException {
            String toolDbKey = in.readString();
            CreativeToolType toolType = CreativeToolType.BY_DBKEY.get(toolDbKey, CreativeToolType.UNKNOWN);
            String changeNameMM = in.readString();

            Component text = in.readComponent();
            SoundEvent sound = SoundEvent.fromKey(in.readString());
            if (sound == null) {
                sound = SoundEvent.BLOCK_NOTE_BLOCK_PLING;
            }
            double soundPitch = in.readDouble();
            return new ChangeMeta(toolType, changeNameMM, text, sound, soundPitch);
        }
    }
}
