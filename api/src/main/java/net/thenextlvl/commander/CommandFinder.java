package net.thenextlvl.commander;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The CommandFinder interface defines methods for finding commands based on a given input.
 */
@NullMarked
public interface CommandFinder {
    /**
     * Finds and returns a set of commands that match the given pattern.
     *
     * @param pattern The pattern used to search for commands.
     * @return An unmodifiable set of strings representing the commands that match the pattern.
     */
    @Unmodifiable
    Set<String> findCommands(Pattern pattern);

    /**
     * Filters and finds commands from the provided stream that match the given pattern.
     *
     * @param commands The stream of commands to be searched.
     * @param pattern  The pattern used to filter matching commands.
     * @return An unmodifiable set of strings representing the commands that match the given pattern.
     */
    default @Unmodifiable Set<String> findCommands(Stream<String> commands, Pattern pattern) {
        return commands.filter(command ->
                pattern.matcher(command).matches()
        ).collect(Collectors.toSet());
    }

    /**
     * This method finds commands based on a given input.
     *
     * @param commands The stream of commands to search for.
     * @param input    The input used to search for commands.
     * @return An unmodifiable set of strings representing the found commands.
     */
    default @Unmodifiable Set<String> findCommands(Stream<String> commands, String input) {
        try {
            return findCommands(commands, Pattern.compile(input));
        } catch (PatternSyntaxException e) {
            return findCommands(commands, Pattern.compile(Pattern.quote(input)));
        }
    }

    /**
     * Finds commands based on the given input.
     *
     * @param input The input used to search for commands.
     * @return An unmodifiable set of strings representing the found commands.
     */
    default @Unmodifiable Set<String> findCommands(String input) {
        try {
            return findCommands(Pattern.compile(input));
        } catch (PatternSyntaxException e) {
            return findCommands(Pattern.compile(Pattern.quote(input)));
        }
    }
}
