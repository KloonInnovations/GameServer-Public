package io.kloon.gameserver.modes.creative.commands.patterns;

import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PatternParseException extends ArgumentSyntaxException {
    private final String inputPiece;
    private final String errorMsg;

    public PatternParseException(String inputPiece, String errorMsg) {
        super(errorMsg, inputPiece, 0);
        this.inputPiece = inputPiece;
        this.errorMsg = errorMsg;
    }

    public void sendError(KloonPlayer player) {
        player.sendPit(NamedTextColor.RED, "UH?", MM."<gray>Couldn't parse that block or pattern!");
        player.sendMessage(MM."<white>Error: <red>\{inputPiece}");
        player.sendMessage(MM."<white>Bit of text: <red>\"\{errorMsg}\"");
    }
}
