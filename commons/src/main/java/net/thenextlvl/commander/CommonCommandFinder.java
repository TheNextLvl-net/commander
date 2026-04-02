package net.thenextlvl.commander;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

@NullMarked
public abstract class CommonCommandFinder implements CommandFinder {
    @Override
    public @Unmodifiable Stream<String> findCommands(final Stream<String> commands, final Pattern pattern) {
        return commands.filter(command -> pattern.matcher(command).matches());
    }

    @Override
    public @Unmodifiable Stream<String> findCommands(final Stream<String> commands, final String input) {
        try {
            return findCommands(commands, Pattern.compile(input.replace("*", ".*")));
        } catch (final PatternSyntaxException e) {
            final var escaped = Pattern.quote(input).replace("\\*", "*");
            return findCommands(commands, Pattern.compile(escaped.replace("*", ".*")));
        }
    }

    @Override
    public @Unmodifiable Stream<String> findCommands(final String input) {
        try {
            return findCommands(Pattern.compile(input.replace("*", ".*")));
        } catch (final PatternSyntaxException e) {
            final var escaped = Pattern.quote(input).replace("\\*", "*");
            return findCommands(Pattern.compile(escaped.replace("*", ".*")));
        }
    }
}
