package net.thenextlvl.commander.api;

import core.i18n.file.ComponentBundle;
import net.thenextlvl.commander.api.command.CommandRegistry;
import net.thenextlvl.commander.api.command.PlatformCommandRegistry;
import net.thenextlvl.commander.api.permission.PermissionRegistry;
import net.thenextlvl.commander.api.permission.PlatformPermissionRegistry;

public interface Commander {

    /**
     * @return the component bundle
     */
    ComponentBundle bundle();

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

    interface PlatformRegistry {
        /**
         * @return the platform command registry
         */
        PlatformCommandRegistry<?> commandRegistry();

        /**
         * @return the platform permission registry
         */
        PlatformPermissionRegistry permissionRegistry();
    }
}
