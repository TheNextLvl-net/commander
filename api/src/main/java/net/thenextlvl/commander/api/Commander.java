package net.thenextlvl.commander.api;

import net.thenextlvl.commander.api.command.CommandRegistry;
import net.thenextlvl.commander.api.command.PlatformCommandRegistry;
import net.thenextlvl.commander.api.permission.PermissionRegistry;
import net.thenextlvl.commander.api.permission.PlatformPermissionRegistry;

public abstract class Commander {
    /**
     * @return The command registry
     */
    public abstract CommandRegistry commandRegistry();

    /**
     * @return The platform registry
     */
    public abstract PlatformCommandRegistry<?> platformCommandRegistry();

    /**
     * @return The permission manager
     */
    public abstract PermissionRegistry permissionRegistry();

    /**
     * @return The platform permission manager
     */
    public abstract PlatformPermissionRegistry platformPermissionRegistry();
}
