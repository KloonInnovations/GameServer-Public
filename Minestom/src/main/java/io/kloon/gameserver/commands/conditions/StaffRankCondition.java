package io.kloon.gameserver.commands.conditions;

import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.ranks.StaffRank;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.condition.CommandCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StaffRankCondition implements CommandCondition {
    private final StaffRank staffRank;

    public StaffRankCondition(StaffRank staffRank) {
        this.staffRank = staffRank;
    }

    @Override
    public boolean canUse(@NotNull CommandSender sender, @Nullable String commandString) {
        if (!(sender instanceof KloonPlayer kp)) return false;

        return kp.getRanks().hasPlayerRankOrBetter(staffRank);
    }
}
