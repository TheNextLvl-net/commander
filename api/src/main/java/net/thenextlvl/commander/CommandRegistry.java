package net.thenextlvl.commander;

import net.thenextlvl.binder.StaticBinder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

@ApiStatus.NonExtendable
public interface CommandRegistry {
    CommandRegistry INSTANCE = StaticBinder.getInstance(CommandRegistry.class.getClassLoader()).find(CommandRegistry.class);

    /**
     * Retrieves the set of commands that are currently hidden from visibility.
     * <p>
     * Hidden commands are still accessible and executable but are not displayed
     * in suggestions or tab completions under normal conditions.
     *
     * @return an unmodifiable set of strings representing the commands that are currently hidden
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<String> hiddenCommands();

    /**
     * Retrieves the set of commands that are currently unregistered in the system.
     *
     * @return an unmodifiable set of strings representing the commands that are unregistered
     */
    @Unmodifiable
    @Contract(pure = true)
    Set<String> unregisteredCommands();

    /**
     * Hides the specified command.
     * <p>
     * Hidden commands can be accessed as normal but are not shown during tab completion.
     * They remain visible to users who possess the appropriate bypass permission.
     *
     * @param command the name of the command to hide
     * @return true if the command was hidden, false if the command was already hidden or doesn't exist
     */
    boolean hide(String command);

    /**
     * Determines if the specified command is currently hidden.
     *
     * @param command the name of the command to check
     * @return true if the command is hidden, false otherwise
     */
    @Contract(pure = true)
    boolean isHidden(String command);

    /**
     * Checks if the specified command is currently unregistered.
     *
     * @param command the name of the command to check
     * @return true if the command is unregistered, false otherwise
     */
    @Contract(pure = true)
    boolean isUnregistered(String command);

    /**
     * Registers the specified command with the command registry.
     * <p>
     * This method attempts to add the given command back into the system's registry,
     * making it available for usage if it was previously unregistered.
     *
     * @param command the name of the command to register
     * @return true if the command was successfully registered,
     * false if the command was already registered or failed to register
     */
    boolean register(String command);

    /**
     * Reveals the specified command.
     * <p>
     * This method makes a previously hidden command visible again for suggestions and tab completions.
     *
     * @param command the name of the command to reveal
     * @return true if the command was successfully revealed,
     * false if the command was already visible or does not exist
     */
    boolean reveal(String command);

    /**
     * Unregisters the specified command from the command registry.
     * This method removes the command, making it unavailable for usage.
     *
     * @param command the name of the command to unregister
     * @return true if the command was successfully unregistered,
     * false if the command was already unregistered or does not exist
     */
    boolean unregister(String command);

    /**
     * Unregisters all currently registered commands in the command registry.
     * <p>
     * This method removes all commands from the registry, making them unavailable for execution
     * and preventing them from appearing in any suggestions or tab completions.
     * It is typically used during plugin lifecycle events to reset or clear the command state.
     */
    void unregisterCommands();
}
