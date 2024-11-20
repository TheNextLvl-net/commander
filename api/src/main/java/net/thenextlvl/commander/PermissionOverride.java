package net.thenextlvl.commander;

import net.kyori.adventure.audience.Audience;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
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

    boolean reload(Audience audience);
}
