package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

public class CommandArgumentType extends WrappedArgumentType<String, String> {
    public CommandArgumentType(CommanderPlugin plugin) {
        super(StringArgumentType.string(), (reader, type) -> type,
                (context, suggestions) -> {
                    Bukkit.getCommandMap().getKnownCommands().values().stream()
                            .map(Command::getLabel)
                            .filter(s -> !plugin.commandRegistry().isUnregistered(s))
                            .map(StringArgumentType::escapeIfRequired)
                            .filter(s -> s.contains(suggestions.getRemaining()))
                            .forEach(suggestions::suggest);
                    return suggestions.buildFuture();
                });
    }
}
