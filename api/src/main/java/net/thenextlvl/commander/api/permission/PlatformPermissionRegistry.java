package net.thenextlvl.commander.api.permission;

import net.thenextlvl.commander.api.Commander;
import org.jetbrains.annotations.Nullable;

public interface PlatformPermissionRegistry {
    Commander commander();

    default void overridePermissions() {
        commander().permissionRegistry().getPermissionOverride().forEach(this::overridePermission);
    }

    boolean overridePermission(String literal, @Nullable String permission);

    @Nullable String getOriginalPermission(String literal);

    boolean hasOriginalPermission(String literal);
}
