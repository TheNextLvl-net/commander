package net.thenextlvl.commander;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

@ApiStatus.NonExtendable
public interface Commander {
    /**
     * Retrieves the CommandFinder instance associated with the Commander.
     *
     * @return the CommandFinder instance
     */
    @Contract(pure = true)
    CommandFinder commandFinder();

    /**
     * Retrieves the CommandRegistry instance associated with the Commander.
     *
     * @return the CommandRegistry instance
     */
    @Contract(pure = true)
    CommandRegistry commandRegistry();

    /**
     * Get the permission override instance.
     * This method returns a PermissionOverride instance, which is used to manipulate permissions for commands.
     *
     * @return the PermissionOverride instance
     */
    @Contract(pure = true)
    PermissionOverride permissionOverride();
}
