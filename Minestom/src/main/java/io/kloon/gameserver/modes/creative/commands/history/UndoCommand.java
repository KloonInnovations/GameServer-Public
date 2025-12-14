package io.kloon.gameserver.modes.creative.commands.history;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeRecord;
import io.kloon.gameserver.modes.creative.history.History;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.history.results.*;
import io.kloon.gameserver.modes.creative.history.results.history.EndOfHistoryResult;
import io.kloon.gameserver.modes.creative.history.results.history.HistoryChangedResult;
import io.kloon.gameserver.modes.creative.history.results.history.HistoryEditResult;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class UndoCommand extends Command {
    private static final Logger LOG = LoggerFactory.getLogger(UndoCommand.class);

    public static final String LABEL = "undo";

    public UndoCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                undo(player);
            }
        });
    }

    public static void undo(CreativePlayer player) {
        if (!player.canEditWorld(true)) {
            return;
        }

        try {
            History playerHistory = player.getHistory();
            if (playerHistory.hasOngoingChanges()) {
                player.msg().send(MsgCat.HISTORY,
                        NamedTextColor.WHITE, "NOT YET!", MM."<gray>Can't undo while there are ongoing changes!",
                        SoundEvent.ITEM_SHIELD_BLOCK, 0.5);
                return;
            }

            player.getChangesByHand().flush();

            HistoryEditResult editResult = playerHistory.undo(player);
            if (editResult instanceof EndOfHistoryResult) {
                player.msg().send(MsgCat.HISTORY,
                        NamedTextColor.LIGHT_PURPLE, "CAN'T UNDO", MM."<gray>There's nothing past this in the history!",
                        SoundEvent.ITEM_SHIELD_BLOCK, 0.5);
                return;
            } if (!(editResult instanceof HistoryChangedResult changed)) {
                player.sendPit(NamedTextColor.RED, "CAN'T UNDO", MM."<gray>Dunno what changed in the history! Awkward!");
                return;
            }

            ChangeRecord undone = changed.initialRecord();
            ChangeResult result = changed.changeResult();
            handleChangeResult(player, undone, result);
        } catch (Throwable t) {
            LOG.error("Error with undo", t);
            player.sendPitError(MM."<gray>There was an error unwinding the latest change!");
        }
    }

    private static void handleChangeResult(CreativePlayer player, ChangeRecord undone, ChangeResult result) {
        CreativeInstance instance = player.getInstance();

        if (result instanceof ChangeResultList list) {
            for (ChangeResult resultElem : list.get()) {
                if (resultElem instanceof ExceptionResult error) {
                    handleError(player, undone, error);
                } else if (resultElem instanceof WorkChangeResult workChange) {
                    instance.getJobQueue().trySubmit("Undo", CreativeToolType.HISTORY, player, workChange.work());
                }
            }
            instance.addToAuditHistory(getUndoAudit(player, undone));
            sendReverseEffects(player, undone.meta());
        } else if (result instanceof ExceptionResult error) {
            handleError(player, undone, error);
        } else if (result instanceof WorkChangeResult workChange) {
            instance.addToAuditHistory(getUndoAudit(player, undone));
            BlocksJob undoJob = instance.getJobQueue().trySubmit("Undo", CreativeToolType.HISTORY, player, workChange.work());
            if (undoJob == null) {
                player.getHistory().add(undone, false);
                player.msg().send(MsgCat.HISTORY,
                        NamedTextColor.LIGHT_PURPLE, "HISTORY", MM."<gray>Couldn't queue job, re-added to undo history!",
                        SoundEvent.ENTITY_VILLAGER_WORK_MASON, 0.7);
            } else {
                sendReverseEffects(player, undone.meta());
            }
        } else if (result instanceof InstantResult) {
            instance.addToAuditHistory(getUndoAudit(player, undone));
            sendReverseEffects(player, undone.meta());
        } else {
            player.sendPitError(MM."<white>Unhandled change result in undo!");
        }
    }

    private static void handleError(CreativePlayer player, ChangeRecord undone, ExceptionResult error) {
        Throwable exception = error.throwable();
        LOG.error(STR."ExceptionResult from undoing \{undone.change().getType()}", exception);
        player.playSound(SoundEvent.ENTITY_WOLF_WHINE, 1.1);
        player.sendPitError(MM."<gray>There was an error applying the undo!");
    }

    private static AuditRecord getUndoAudit(CreativePlayer author, ChangeRecord undone) {
        ChangeMeta undoneMeta = undone.meta();
        ChangeMeta meta = new ChangeMeta(
                CreativeToolType.HISTORY,
                STR."<white>Undo: \{undoneMeta.changeTitleMM()}",
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
        double pitch = initialPitch >= 1.0 ? 0.5 : 2.0;
        player.broadcast().send(MsgCat.HISTORY,
                NamedTextColor.RED, "UNDO!", meta.chatText().decorate(TextDecoration.STRIKETHROUGH).color(NamedTextColor.GRAY),
                meta.sound(), Pitch.base(pitch), 0.8);
    }
}
