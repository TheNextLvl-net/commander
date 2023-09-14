package net.thenextlvl.commander.implementation.permission;

import net.thenextlvl.commander.api.permission.PlatformPermissionRegistry;
import net.thenextlvl.commander.implementation.CraftCommander;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record CraftPlatformPermissionRegistry(CraftCommander commander) implements PlatformPermissionRegistry {

    @Override
    public boolean overridePermission(String literal, @Nullable String permission) {
        var target = Bukkit.getCommandMap().getCommand(literal);
        if (target == null) return false;
        commander().permissionRegistry().getOriginalPermissions()
                .putIfAbsent(target.getName(), target.getPermission());
        if (Objects.equals(target.getPermission(), permission)) return false;
        target.setPermission(permission);
        return true;
    }

    @Override
    public @Nullable String getOriginalPermission(String literal) {
        var command = commander().platformCommandRegistry().getCommand(literal);
        return command != null ? commander().permissionRegistry().getOriginalPermissions().get(command.getName()) : null;
    }

    @Override
    public boolean hasOriginalPermission(String literal) {
        var originalPermissions = commander().permissionRegistry().getOriginalPermissions();
        if (originalPermissions.containsKey(literal)) return true;
        var command = commander().platformCommandRegistry().getCommand(literal);
        return command != null && originalPermissions.containsKey(command.getName());
    }
}
