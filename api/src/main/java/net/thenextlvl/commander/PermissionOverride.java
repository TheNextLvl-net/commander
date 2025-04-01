package net.thenextlvl.commander;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public interface PermissionOverride {
    @Unmodifiable
    Map<String, @Nullable String> originalPermissions();

    @Unmodifiable
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
