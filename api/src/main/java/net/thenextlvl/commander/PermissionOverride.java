package net.thenextlvl.commander;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public interface PermissionOverride {
    /**
     * Retrieves a mapping of commands and their associated original permissions.
     * <p>
     * The map provides a view of commands with the permissions initially assigned
     * to them before any modifications or overrides were applied.
     *
     * @return an unmodifiable map containing command names as keys and their initially assigned permissions as values.
     * The permission value may be null if no original permission was assigned.
     */
    @Unmodifiable
    Map<String, @Nullable String> originalPermissions();

    /**
     * Retrieves a mapping of commands and their associated permission overrides.
     * <p>
     * The map provides a view of commands with permissions that have been modified
     * or overridden from their original values.
     *
     * @return an unmodifiable map containing command names as keys and their overridden permissions as values.
     * The permission value may be null if no override is applied to the command.
     */
    @Unmodifiable
    Map<String, @Nullable String> overrides();

    /**
     * Retrieves the original permission assigned to the specified command before any overrides or modifications.
     *
     * @param command the name of the command whose original permission is to be retrieved
     * @return the originally assigned permission as a string, or null if no original permission exists for the command
     */
    @Nullable
    String originalPermission(String command);

    /**
     * Retrieves the current effective permission associated with the specified command.
     * The returned permission reflects any overrides that may have been applied.
     *
     * @param command the name of the command for which to retrieve the permission
     * @return the effective permission as a string, or null if no permission is assigned to the command
     */
    @Nullable
    String permission(String command);

    /**
     * Determines if the specified command has its permission overridden.
     * A command is considered overridden if it has a modified permission that differs from its original assignment.
     *
     * @param command the name of the command to check
     * @return true if the command's permission is overridden, false otherwise
     */
    boolean isOverridden(String command);

    /**
     * Overrides the permission associated with a command.
     * <p>
     * If the provided permission is null, the method attempts to remove the original permission,
     * effectively unassigning any existing permission.
     *
     * @param command    the name of the command whose permission is to be overridden
     * @param permission the new permission to assign to the command, or null to remove
     * @return true if the operation was successful, false otherwise
     */
    boolean override(String command, @Nullable String permission);

    /**
     * Reverts the permission of the given command to its original state before any overrides were applied.
     *
     * @param command the name of the command whose permission is to be reset
     * @return true if the permission override was successfully reset, false otherwise
     */
    boolean reset(String command);

    /**
     * Applies permission overrides to all commands.
     * <p>
     * This method enforces the overridden permissions configured in the system,
     * replacing the original permissions for all applicable commands.
     * Any subsequent retrieval of permissions will reflect the overridden values.
     */
    void overridePermissions();
}
