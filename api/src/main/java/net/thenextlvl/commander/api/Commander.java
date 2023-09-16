package net.thenextlvl.commander.api;

import net.thenextlvl.commander.api.command.CommandRegistry;
import net.thenextlvl.commander.api.command.PlatformCommandRegistry;
import net.thenextlvl.commander.api.permission.PermissionRegistry;
import net.thenextlvl.commander.api.permission.PlatformPermissionRegistry;

public interface Commander {
    /**
     * @return the command registry
     */
    CommandRegistry commandRegistry();

    /**
     * @return the permission registry
     */
    PermissionRegistry permissionRegistry();

    /**
     * @return the platform registries
     */
    PlatformRegistry platform();

    /**
     * @return The platform permission manager
     */
    public abstract PlatformPermissionRegistry platformPermissionRegistry();
}
