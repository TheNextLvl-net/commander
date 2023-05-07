package net.thenextlvl.commander.implementation;

import net.thenextlvl.commander.api.PermissionManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class CraftPermissionManager extends PermissionManager {
    public CraftPermissionManager(File dataFolder) {
        super(dataFolder);
    }

    @Override
    public void overridePermission(String command, @Nullable String permission, boolean alias) {
        var target = Bukkit.getCommandMap().getCommand(command);
        if (target == null) return;
        if (!alias) target.getAliases().forEach(s -> overridePermission(s, permission, true));
        target.setPermission(permission);
    }
}
