package io.kloon.gameserver.modes.creative.ux.messaging;

public enum MsgCat { // MessageCategory
    INVENTORY(false),
    TOOL(false),
    POSITION(true),
    HISTORY(false),
    PREFERENCE(true),
    WORLD(true),
    JOBS(true),
    NEGATIVE(true),
    ;

    private final boolean forceSendToChat;

    MsgCat(boolean forceSendToChat) {
        this.forceSendToChat = forceSendToChat;
    }

    public boolean isAlwaysSentToChat() {
        return forceSendToChat;
    }
}
