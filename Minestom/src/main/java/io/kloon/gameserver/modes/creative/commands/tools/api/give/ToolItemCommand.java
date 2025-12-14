package io.kloon.gameserver.modes.creative.commands.tools.api.give;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.commands.patterns.ArgumentPattern;
import io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt.ToolBlockExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt.ToolNumberExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt.ToolPreferenceExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt.ToolSettingExecutor;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentNumber;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class ToolItemCommand<T extends CreativeTool> extends Command {
    protected final T tool;

    public ToolItemCommand(T tool) {
        super(tool.getType().getCommandLabel());
        this.tool = tool;

        setDefaultExecutor(new GiveToolExecutor(tool));
    }

    public Lore usagelore(String commandName) {
        Lore lore = new Lore();
        lore.add(MM."<dark_gray>\uD83D\uDDAE <green>/\{commandName} <params...>"); // 🖮
        lore.wrap("<gray>Modifies held tool, or adds configured tool to hotbar.");
        return lore;
    }

    protected final void addSyntaxBlockToFillWith() {
        ArgumentPattern patternArg = ArgumentPattern.create("block to fill with");
        addSyntax(new ToolBlockExecutor<>(tool, patternArg), ArgumentType.Literal("block"), patternArg);

//        ArgumentPattern stringArg = ArgumentPattern.create("pattern to fill with").string();
//        addSyntax(new ToolBlockExecutor<>(tool, stringArg), ArgumentType.Literal("block"), stringArg);
    }

    protected final void addSyntaxNumber(String label, String argName, NumberInput number) {
        ArgumentNumber<Double> arg = ArgumentType.Double(argName).min(number.min()).max(number.max());
        addSyntax(new ToolNumberExecutor<>(tool, number, arg),
                ArgumentType.Literal(label), arg);
    }

    protected final void addSyntaxToggleSetting(String label, ToolToggle toggle) {
        addSyntax(new ToolSettingExecutor<>(tool, toggle),
                ArgumentType.Literal(label));
    }

    protected final void addSyntaxTogglePreference(String label, ToolToggle toggle) {
        addSyntax(new ToolPreferenceExecutor<>(tool, toggle),
                ArgumentType.Literal(label));
    }
}
