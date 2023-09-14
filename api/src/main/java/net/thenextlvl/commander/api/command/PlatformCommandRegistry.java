package net.thenextlvl.commander.api.command;

import net.thenextlvl.commander.api.Commander;
import org.intellij.lang.annotations.RegExp;

import java.util.Collection;

public interface PlatformCommandRegistry<C> {

    /**
     * Unregister all {@link CommandRegistry#getRemovedCommands() removed commands}
     */
    default void unregisterCommands() {
        commander().commandRegistry().getRemovedCommands().forEach(this::unregisterCommands);
    }

    /**
     * Unregister removed commands based on a query pattern
     *
     * @param pattern the pattern to apply
     */
    default void unregisterCommands(@RegExp String pattern) {
        commander().commandRegistry().getRemovedCommands(pattern).forEach(this::unregisterCommand);
    }

    /**
     * Unregister a registered command instance by its literal
     *
     * @param literal unregister a command by its literal
     * @return whether the command was registered before
     */
    default boolean unregisterCommand(String literal) {
        return unregisterCommand(getCommand(literal));
    }

    /**
     * Unregister a registered command instance
     *
     * @param command the command to unregister
     * @return whether the command was registered before
     */
    boolean unregisterCommand(C command);

    /**
     * Get all registered command instanced
     *
     * @return a set of command instances
     */
    Collection<C> getCommands();

    /**
     * Get all registered command instances based on a query pattern
     *
     * @param pattern the pattern to apply
     * @return a set of command instances
     */
    Collection<C> getCommands(@RegExp String pattern);

    /**
     * Get a registered command instance by its literal
     *
     * @param literal the command literal
     * @return the command instance
     */
    C getCommand(String literal);

    /**
     * Check whether a command is registered
     *
     * @param literal the command literal
     * @return whether the command is registered
     */
    default boolean isCommandRegistered(String literal) {
        return getCommand(literal) != null;
    }

    /**
     * Check whether both commands match
     *
     * @param first  the first command
     * @param second the second command
     * @return whether the commands match
     */
    boolean matches(C first, C second);

    Commander commander();
}
