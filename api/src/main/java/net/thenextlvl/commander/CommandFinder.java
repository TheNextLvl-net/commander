package net.thenextlvl.commander;

import java.util.Set;
import java.util.stream.Stream;

/**
 * The CommandFinder interface defines methods for finding commands based on a given input.
 */
public interface CommandFinder {
    /**
     * Finds commands based on the given input.
     *
     * @param input The input used to search for commands.
     * @return A set of strings representing the found commands.
     */
    Set<String> findCommands(String input);

    /**
     * This method finds commands based on a given input.
     *
     * @param commands The stream of commands to search for.
     * @param input    The input used to search for commands.
     * @return A set of strings representing the found commands.
     */
    Set<String> findCommands(Stream<String> commands, String input);
}
