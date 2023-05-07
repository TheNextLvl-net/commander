package net.thenextlvl.commander.implementation;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.thenextlvl.commander.api.Commander;

import java.io.File;

@Getter
@Accessors(fluent = true)
public class CraftCommander extends Commander {
    private final CraftCommandManager commandManager;
    private final CraftPermissionManager permissionManager;

    public CraftCommander(File dataFolder) {
        commandManager = new CraftCommandManager(dataFolder);
        permissionManager = new CraftPermissionManager(dataFolder);
    }
}
