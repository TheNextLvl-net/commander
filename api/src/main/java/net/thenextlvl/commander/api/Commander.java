package net.thenextlvl.commander.api;

import net.thenextlvl.commander.i18n.Placeholders;

public abstract class Commander {
    /**
     * @return The command manager
     */
    public abstract CommandManager commandManager();

    /**
     * @return The permission manager
     */
    public abstract PermissionManager permissionManager();

    static {
        Placeholders.init();
    }
}
