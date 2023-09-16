package net.thenextlvl.commander.implementation;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.thenextlvl.commander.CommanderPlugin;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.implementation.command.ProxyCommandRegistry;
import net.thenextlvl.commander.implementation.command.ProxyPlatformCommandRegistry;
import net.thenextlvl.commander.implementation.permission.ProxyPermissionRegistry;
import net.thenextlvl.commander.implementation.permission.ProxyPlatformPermissionRegistry;

@Getter
@Accessors(fluent = true)
public class ProxyCommander implements Commander {
    private final ProxyCommandRegistry commandRegistry;
    private final ProxyPermissionRegistry permissionRegistry;
    private final CraftPlatformRegistry platform;
    private final CommanderPlugin plugin;

    public ProxyCommander(CommanderPlugin plugin) {
        commandRegistry = new ProxyCommandRegistry(this, plugin);
        permissionRegistry = new ProxyPermissionRegistry(this, plugin);
        platform = new CraftPlatformRegistry();
        this.plugin = plugin;
    }

    @Getter
    @Accessors(fluent = true)
    public class CraftPlatformRegistry implements PlatformRegistry {
        private final ProxyPlatformCommandRegistry commandRegistry;
        private final ProxyPlatformPermissionRegistry permissionRegistry;

        private CraftPlatformRegistry() {
            commandRegistry = new ProxyPlatformCommandRegistry(ProxyCommander.this);
            permissionRegistry = new ProxyPlatformPermissionRegistry(ProxyCommander.this);
        }
    }
}
