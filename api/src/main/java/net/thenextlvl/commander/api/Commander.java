package net.thenextlvl.commander.api;

import core.annotation.MethodsReturnNotNullByDefault;
import core.i18n.file.ComponentBundle;
import net.thenextlvl.commander.api.platform.CommandManager;
import net.thenextlvl.commander.api.platform.PermissionManager;

@MethodsReturnNotNullByDefault
public interface Commander<C> {

    /**
     * @return the component bundle
     */
    ComponentBundle bundle();

    /**
     * @return the command information registry
     */
    CommandRegistry commandRegistry();

    /**
     * @return the platform command manager
     */
    CommandManager<C> commandManager();

    /**
     * @return the platform permission manager
     */
    PermissionManager<C> permissionManager();
}
