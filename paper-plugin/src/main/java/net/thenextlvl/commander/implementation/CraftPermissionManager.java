package net.thenextlvl.commander.implementation;

import net.thenextlvl.commander.api.PermissionManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CraftPermissionManager extends PermissionManager {
    private final Map<String, String> originalPermissions = new HashMap<>();

    public CraftPermissionManager(File dataFolder) {
        super(dataFolder);
    }

    @Override
    public boolean overridePermission(String command, @Nullable String permission, boolean alias) {
        var target = Bukkit.getCommandMap().getCommand(command);
        if (target == null) return false;
        if (!alias) target.getAliases().forEach(s -> overridePermission(s, permission, true));
        originalPermissions.putIfAbsent(target.getName(), target.getPermission());
        if (Objects.equals(target.getPermission(), permission)) return false;
        target.setPermission(permission);
        return true;
    }

    @Override
    public @Nullable String getOriginalPermission(String label) {
        var command = Bukkit.getCommandMap().getCommand(label);
        return command != null ? originalPermissions.get(command.getName()) : null;
    }

    @Override
    public boolean hasOriginalPermission(String label) {
        if (originalPermissions.containsKey(label)) return true;
        var command = Bukkit.getCommandMap().getCommand(label);
        return command != null && originalPermissions.containsKey(command.getName());
    }
}
