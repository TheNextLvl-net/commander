package net.thenextlvl.commander.api.platform;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.commander.api.Commander;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public interface CommandManager<C> {

    /**
     * Get the namespaces of all registered commands
     *
     * @return a stream of command namespaces
     */
    Stream<String> getCommandNames();

    /**
     * Get all registered command instances
     *
     * @return a collection of command instances
     */
    Collection<C> getCommands();

    /**
     * Get all registered command instances matching a certain query
     *
     * @return a stream of command instances
     */
    Stream<C> getCommands(String query);

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

    Commander<C> commander();
}
