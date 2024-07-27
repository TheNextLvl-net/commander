package net.thenextlvl.commander.paper.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class CommandSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    private final CommanderPlugin plugin;

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
                Bukkit.getCommandMap().getKnownCommands().values().stream()
                        .map(Command::getLabel)
                        .filter(s -> !plugin.commandRegistry().isUnregistered(s))
                        .map(StringArgumentType::escapeIfRequired)
                        .filter(s -> s.contains(builder.getRemaining()))
                        .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
