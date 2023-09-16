package net.thenextlvl.commander.implementation.permission;

import net.thenextlvl.commander.api.permission.PermissionRegistry;
import net.thenextlvl.commander.implementation.CraftCommander;

import java.io.File;

public class CraftPermissionRegistry extends PermissionRegistry {
    public CraftPermissionRegistry(CraftCommander commander, File dataFolder) {
        super(commander, dataFolder);
    }
}
