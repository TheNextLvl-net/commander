package net.thenextlvl.commander;

import core.i18n.file.ComponentBundle;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Commander {
    /**
     * Retrieves the CommandFinder instance associated with the Commander.
     *
     * @return the CommandFinder instance
     */
    CommandFinder commandFinder();

    /**
     * Retrieves the ComponentBundle associated with the Commander.
     *
     * @return the ComponentBundle instance
     */
    ComponentBundle bundle();

    /**
     * Retrieves the CommandRegistry instance associated with the Commander.
     *
     * @return the CommandRegistry instance
     */
    CommandRegistry commandRegistry();

    /**
     * Get the permission override instance.
     * This method returns a PermissionOverride instance, which is used to manipulate permissions for commands.
     *
     * @return the PermissionOverride instance
     */
    PermissionOverride permissionOverride();
}
