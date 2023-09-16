package net.thenextlvl.commander.implementation.permission;

import net.thenextlvl.commander.api.permission.PlatformPermissionRegistry;
import net.thenextlvl.commander.implementation.CraftCommander;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record CraftPlatformPermissionRegistry(CraftCommander commander) implements PlatformPermissionRegistry {

    @Override
    public boolean overridePermission(String literal, @Nullable String permission) {
        return commander.platform().commandRegistry().getCommand(literal).map(command -> {
            commander().permissionRegistry().getOriginalPermissions()
                    .putIfAbsent(command.getLabel(), command.getPermission());
            if (Objects.equals(command.getPermission(), permission)) return false;
            command.setPermission(permission);
            return true;
        }).orElse(false);
    }

    @Override
    public @Nullable String getOriginalPermission(String literal) {
        return commander().platform().commandRegistry().getCommand(literal)
                .map(command -> commander().permissionRegistry().getOriginalPermissions().get(command.getLabel()))
                .orElse(null);
    }

    @Override
    public boolean hasOriginalPermission(String literal) {
        var originalPermissions = commander().permissionRegistry().getOriginalPermissions();
        if (originalPermissions.containsKey(literal)) return true;
        return commander().platform().commandRegistry().getCommand(literal)
                .map(command -> originalPermissions.containsKey(command.getLabel()))
                .orElse(true);
    }
}
