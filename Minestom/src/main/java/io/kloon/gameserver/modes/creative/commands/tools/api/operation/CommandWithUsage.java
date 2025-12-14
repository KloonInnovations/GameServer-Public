package io.kloon.gameserver.modes.creative.commands.tools.api.operation;

import java.util.List;

public interface CommandWithUsage {
    List<ToolOperationUsage> getUsages();
}
