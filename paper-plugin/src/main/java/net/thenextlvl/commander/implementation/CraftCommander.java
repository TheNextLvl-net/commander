package net.thenextlvl.commander.implementation;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.thenextlvl.commander.api.Commander;
import net.thenextlvl.commander.implementation.command.CraftCommandRegistry;
import net.thenextlvl.commander.implementation.command.CraftPlatformCommandRegistry;
import net.thenextlvl.commander.implementation.permission.CraftPermissionRegistry;
import net.thenextlvl.commander.implementation.permission.CraftPlatformPermissionRegistry;

import java.io.File;

@Getter
@Accessors(fluent = true)
public class CraftCommander implements Commander {
    private final CraftCommandRegistry commandRegistry;
    private final CraftPermissionRegistry permissionRegistry;
    private final CraftPlatformRegistry platform;

    public CraftCommander(File dataFolder) {
        commandRegistry = new CraftCommandRegistry(this, dataFolder);
        permissionRegistry = new CraftPermissionRegistry(this, dataFolder);
        platform = new CraftPlatformRegistry();
    }

    @Getter
    @Accessors(fluent = true)
    public class CraftPlatformRegistry implements PlatformRegistry {
        private final CraftPlatformCommandRegistry commandRegistry;
        private final CraftPlatformPermissionRegistry permissionRegistry;

        private CraftPlatformRegistry() {
            commandRegistry = new CraftPlatformCommandRegistry(CraftCommander.this);
            permissionRegistry = new CraftPlatformPermissionRegistry(CraftCommander.this);
        }
    }
}
