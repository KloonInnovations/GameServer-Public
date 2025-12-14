package io.kloon.gameserver.modes.creative.tasks;

import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SaveOnceInAWhile implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SaveOnceInAWhile.class);

    private final CreativeInstance instance;

    public SaveOnceInAWhile(CreativeInstance instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        String worldID = instance.getWorldDef().idHex();
        LOG.info(STR."Auto-saving world \{worldID}...");
        instance.withOldSaveDontSave(false);
        instance.saveInstance(WorldSave.Reason.AUTOSAVE).whenComplete((_, t) -> {
            if (t == null) {
                LOG.info(STR."Auto-saved world \{worldID}...");
                instance.sendMessage(MM."<green><b>SAVED!</b> <gray>The world has been auto-saved!");
            } else {
                instance.sendMessage(MM."<dark_red>SAVE ERROR! <red>Error saving the world! That's uh... bad!");
                LOG.error(STR."Error saving instance \{worldID}", t);
            }
        });
    }
}
