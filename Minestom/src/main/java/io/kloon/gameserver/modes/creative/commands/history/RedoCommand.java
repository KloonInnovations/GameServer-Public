package io.kloon.gameserver.modes.creative.commands.history;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.history.*;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.history.results.*;
import io.kloon.gameserver.modes.creative.history.results.history.EndOfHistoryResult;
import io.kloon.gameserver.modes.creative.history.results.history.HistoryChangedResult;
import io.kloon.gameserver.modes.creative.history.results.history.HistoryEditResult;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class RedoCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(RedoCommand.class);

    private static final String LABEL = "redo";

    public RedoCommand() {
        super(LABEL);
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                redo(player);
            }
        });
    }

    public static void redo(CreativePlayer player) {
        if (!player.canEditWorld(true)) {
            return;
        }

        try {
            History history = player.getHistory();
            if (history.hasOngoingChanges()) {
                player.msg().send(MsgCat.HISTORY,
                        NamedTextColor.WHITE, "NOT YET!", MM."<gray>Can't redo while there are ongoing changes!",
                        SoundEvent.ITEM_SHIELD_BLOCK, 0.5);
                return;
            }

            player.getChangesByHand().flush();

            HistoryEditResult editResult = history.redo(player);
            if (editResult instanceof EndOfHistoryResult) {
                player.msg().send(MsgCat.HISTORY,
                        NamedTextColor.DARK_PURPLE, "CAN'T REDO!", MM."<gray>There's nothing to redo in the history!",
                        SoundEvent.ITEM_SHIELD_BLOCK, 1.1);
                return;
            } if (!(editResult instanceof HistoryChangedResult changed)) {
                player.msg().send(MsgCat.HISTORY, NamedTextColor.RED, "CAN'T UNDO", MM."<gray>Dunno what changed in the history! Awkward!");
                return;
            }

            ChangeRecord undone = changed.initialRecord();
            ChangeResult result = changed.changeResult();
            handleChangeResult(player, undone, result);
        } catch (Throwable t) {
            LOG.error("Error with redo", t);
            player.sendPit(NamedTextColor.DARK_RED, "ERROR!", MM."<gray>There was an error redoing the change!");
        }
    }

    private static void handleChangeResult(CreativePlayer player, ChangeRecord undone, ChangeResult result) {
        CreativeInstance instance = player.getInstance();

        if (result instanceof ChangeResultList listed) {
            for (ChangeResult resultElem : listed.get()) {
                if (resultElem instanceof ExceptionResult error) {
                    handleError(player, undone, error);
                } else if (resultElem instanceof WorkChangeResult workChange) {
                    instance.getJobQueue().trySubmit("Redo", CreativeToolType.HISTORY, player, workChange.work());
                }
            }
            player.getHistory().add(undone, false);
            instance.addToAuditHistory(getRedoAudit(player, undone));
            sendReverseEffects(player, undone.meta());
        } else if (result instanceof ExceptionResult error) {
            handleError(player, undone, error);
        } else if (result instanceof WorkChangeResult workChange) {
            BlocksJob redoJob = instance.getJobQueue().trySubmit("Redo", CreativeToolType.HISTORY, player, workChange.work());
            if (redoJob == null) {
                player.getHistory().addFuture(undone);
                player.msg().send(MsgCat.HISTORY,
                        NamedTextColor.LIGHT_PURPLE, "HISTORY", MM."<gray>Couldn't queue job, re-added to redo history!",
                        SoundEvent.ENTITY_VILLAGER_WORK_MASON, 0.7);
            } else {
                player.getHistory().add(OngoingChange.fromJob(redoJob, undone.meta()), false);
                instance.addToAuditHistory(getRedoAudit(player, undone));
                sendReverseEffects(player, undone.meta());
            }
        } else if (result instanceof InstantResult) {
            player.getHistory().add(undone, false);
            instance.addToAuditHistory(getRedoAudit(player, undone));
            sendReverseEffects(player, undone.meta());
        } else {
            player.sendPitError(MM."<white>Unhandled change result in redo!");
        }
    }

    private static void handleError(CreativePlayer player, ChangeRecord undone, ExceptionResult error) {
        Throwable exception = error.throwable();
        LOG.error(STR."ExceptionResult from redoing \{undone.change().getType()}", exception);
        player.msg().send(MsgCat.HISTORY,
                NamedTextColor.DARK_RED, "ERROR", MM."<gray>There was an error applying the redo!",
                SoundEvent.ENTITY_WOLF_WHINE, 1.1);
    }

    private static AuditRecord getRedoAudit(CreativePlayer author, ChangeRecord undone) {
        ChangeMeta undoneMeta = undone.meta();
        ChangeMeta meta = new ChangeMeta(
                CreativeToolType.HISTORY,
                STR."<white>Redo: \{undoneMeta.changeTitleMM()}",
                undoneMeta.chatText(),
                undoneMeta.sound(),
                undoneMeta.soundPitch()
        );

        return new AuditRecord(
                System.currentTimeMillis(),
                author.getAccountId(),
                undone.change().getType(),
                meta
        );
    }

    private static void sendReverseEffects(CreativePlayer player, ChangeMeta meta) {
        double initialPitch = meta.soundPitch();
        player.broadcast().send(MsgCat.HISTORY,
                NamedTextColor.YELLOW, "REDO!", meta.chatText().color(NamedTextColor.GRAY),
                meta.sound(), Pitch.base(initialPitch), 0.8);
    }
}
