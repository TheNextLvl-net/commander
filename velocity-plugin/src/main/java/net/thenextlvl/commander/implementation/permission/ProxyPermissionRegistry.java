package net.thenextlvl.commander.implementation.permission;

import net.thenextlvl.commander.CommanderPlugin;
import net.thenextlvl.commander.api.permission.PermissionRegistry;
import net.thenextlvl.commander.implementation.ProxyCommander;

public class ProxyPermissionRegistry extends PermissionRegistry {
    public ProxyPermissionRegistry(ProxyCommander commander, CommanderPlugin plugin) {
        super(commander, plugin.dataFolder().toFile());
    }
}
