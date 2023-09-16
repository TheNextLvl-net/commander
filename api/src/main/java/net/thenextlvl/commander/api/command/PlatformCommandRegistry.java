package net.thenextlvl.commander.api.command;

import net.thenextlvl.commander.api.Commander;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public interface PlatformCommandRegistry<C> {

    /**
     * Get the namespaces of all registered commands
     *
     * @return a stream of command namespaces
     */
    Stream<String> getCommandNamespaces();

    /**
     * Get all registered command instanced
     *
     * @return a set of command instances
     */
    Collection<C> getCommands();

    /**
     * Get a registered command instance by its literal
     *
     * @param literal the command literal
     * @return the command instance
     */
    Optional<C> getCommand(String literal);

    /**
     * Check whether a command is registered
     *
     * @param literal the command literal
     * @return whether the command is registered
     */
    default boolean isCommandRegistered(String literal) {
        return getCommand(literal).isPresent();
    }

    /**
     * Update all commands
     */
    void updateCommands();

    Commander commander();
}
