package net.thenextlvl.commander.velocity.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.velocitypowered.api.command.CommandSource;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.velocity.CommanderPlugin;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
class CommandSuggestionProvider implements SuggestionProvider<CommandSource> {
    private final CommanderPlugin plugin;

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        plugin.server().getCommandManager().getAliases().stream()
                .filter(s -> !plugin.commandRegistry().isUnregistered(s))
                .map(StringArgumentType::escapeIfRequired)
                .filter(s -> s.contains(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
