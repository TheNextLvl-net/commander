package net.thenextlvl.commander.command.suggestion;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.thenextlvl.commander.CommanderCommons;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public class CommandSuggestionProvider<S> implements SuggestionProvider<S> {
    private final CommanderCommons commons;

    public CommandSuggestionProvider(final CommanderCommons commons) {
        this.commons = commons;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        commons.getKnownCommands()
                .filter(s -> !commons.commandRegistry().isUnregistered(s))
                .map(StringArgumentType::escapeIfRequired)
                .filter(s -> s.contains(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
