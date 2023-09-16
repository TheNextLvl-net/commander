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
    private final CraftPlatformCommandRegistry platformCommandRegistry;
    private final CraftPermissionRegistry permissionRegistry;
    private final CraftPlatformPermissionRegistry platformPermissionRegistry;

    public CraftCommander(File dataFolder) {
        commandRegistry = new CraftCommandRegistry(this, dataFolder);
        platformCommandRegistry = new CraftPlatformCommandRegistry(this);
        permissionRegistry = new CraftPermissionRegistry(this, dataFolder);
        platformPermissionRegistry = new CraftPlatformPermissionRegistry(this);
    }
}
