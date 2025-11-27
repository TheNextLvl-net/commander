package net.thenextlvl.commander;

import net.thenextlvl.binder.StaticBinder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The CommandFinder interface defines methods for finding commands based on a given input.
 */
@ApiStatus.NonExtendable
public interface CommandFinder {
    static CommandFinder instance() {
        return StaticBinder.getInstance(CommandFinder.class.getClassLoader()).find(CommandFinder.class);
    }

    /**
     * Finds and returns a set of commands that match the given pattern.
     *
     * @param pattern The pattern used to search for commands.
     * @return A stream of strings representing the commands that match the pattern.
     */
    @Unmodifiable
    @Contract(pure = true)
    Stream<String> findCommands(Pattern pattern);

    /**
     * Filters and finds commands from the provided stream that match the given pattern.
     *
     * @param commands The stream of commands to be searched.
     * @param pattern  The pattern used to filter matching commands.
     * @return A stream of strings representing the commands that match the given pattern.
     */
    @Contract(pure = true)
    Stream<String> findCommands(Stream<String> commands, Pattern pattern);

    /**
     * This method finds commands based on a given input.
     *
     * @param commands The stream of commands to search for.
     * @param input    The input used to search for commands.
     * @return A stream of strings representing the found commands.
     */
    @Contract(pure = true)
    Stream<String> findCommands(Stream<String> commands, String input);

    /**
     * Finds commands based on the given input.
     *
     * @param input The input used to search for commands.
     * @return A stream of strings representing the found commands.
     */
    @Contract(pure = true)
    Stream<String> findCommands(String input);
}
