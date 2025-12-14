package io.kloon.gameserver.player.settings;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.mongo.storage.BufferedDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerSettingsStorage {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerSettingsStorage.class);

    private final KloonPlayer player;
    private final BufferedDocument settingsDoc;

    public PlayerSettingsStorage(KloonPlayer player) {
        this.player = player;
        this.settingsDoc = player.getAccount().getDocument();
    }

    public void setAcceptJoins(boolean acceptJoins) {
        saveBoolean(ACCEPT_JOINS, acceptJoins);
    }

    public boolean isAcceptingJoins() {
        return settingsDoc.getBoolean(ACCEPT_JOINS, true);
    }

    private void saveBoolean(String key, boolean value) {
        settingsDoc.putBoolean(key, value);

        Kgs.INSTANCE.getAccountsRepo().update(player.getAccount()).exceptionally(t -> {
            LOG.error(STR."Error saving account after setting \"\{key}\" to \{value}", t);
            return null;
        });
    }

    private static final String ACCEPT_JOINS = "accept_joins";
}
