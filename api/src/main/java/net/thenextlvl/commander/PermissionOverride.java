package net.thenextlvl.commander;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface PermissionOverride {
    Map<String, @Nullable String> originalPermissions();

    Map<String, @Nullable String> overrides();

    @Nullable
    String originalPermission(String command);

    @Nullable
    String permission(String command);

    boolean isOverridden(String command);

    boolean override(String command, @Nullable String permission);

    boolean reset(String command);

    void overridePermissions();
}
