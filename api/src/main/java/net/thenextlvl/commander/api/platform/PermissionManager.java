package net.thenextlvl.commander.api.platform;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import net.thenextlvl.commander.api.Commander;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public interface PermissionManager<C> {
    /**
     * @return the corresponding commander instance
     */
    Commander commander();

    /**
     * Override all registered permissions
     */
    default void overridePermissions() {
        commander().commandRegistry().getCommandInformation().stream()
                .filter(info -> info.status() == null)
                .forEach(info -> overridePermissions(info.query(), info.permission()));
    }

    /**
     * Reset all command permissions matching the command query
     *
     * @param query the command query
     * @return whether any permission was reset
     */
    boolean resetPermissions(String query);

    /**
     * Reset the permission of a certain command
     *
     * @param command the command instance
     * @return whether the permission was reset
     */
    boolean resetPermission(C command);

    /**
     * Override all command permissions matching the command query
     *
     * @param query      the command query
     * @param permission the new permission
     * @return whether any permission was overridden
     */
    boolean overridePermissions(String query, @Nullable String permission);

    /**
     * Override a certain command's permission
     *
     * @param command    the command instance
     * @param permission the new permission
     * @return whether the permission was overridden
     */
    boolean overridePermission(C command, @Nullable String permission);

    /**
     * Get the original permission of a certain command
     *
     * @param literal the command
     * @return an optional containing the original permission or empty if unchanged
     */
    @Nullable String getOriginalPermission(String literal);

    /**
     * Test whether a command still has its original permission
     *
     * @param literal the command
     * @return whether the command permission was overridden
     */
    boolean hasOriginalPermission(String literal);
}
