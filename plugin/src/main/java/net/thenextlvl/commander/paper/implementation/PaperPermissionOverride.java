package net.thenextlvl.commander.paper.implementation;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.api.PermissionOverride;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class PaperPermissionOverride implements PermissionOverride {
    private final Map<String, @Nullable String> overrides = new HashMap<>();
    private final Map<String, @Nullable String> originalPermissions = new HashMap<>();

    @Override
    public Map<String, @Nullable String> overrides() {
        return Map.copyOf(overrides);
    }

    @Override
    public Map<String, @Nullable String> originalPermissions() {
        return Map.copyOf(originalPermissions);
    }

    @Override
    public @Nullable String originalPermission(String command) {
        return originalPermissions.get(command);
    }

    @Override
    public boolean isOverridden(String command) {
        return !originalPermissions.containsKey(command);
    }

    @Override
    public boolean override(String command, @Nullable String permission) {
        overrides.put(command, permission);
        return internalOverride(command, permission);
    }

    @Override
    public boolean reset(String command) {
        if (!overrides.containsKey(command)) return false;
        overrides.remove(command);
        return internalReset(command);
    }

    @Override
    public void overridePermissions() {
        overrides.forEach(this::internalOverride);
    }

    private boolean internalOverride(String command, @Nullable String permission) {
        var registered = Bukkit.getCommandMap().getKnownCommands().get(command);
        if (registered == null) return false;
        if (Objects.equals(registered.getPermission(), permission)) return false;
        originalPermissions.putIfAbsent(command, registered.getPermission());
        registered.setPermission(permission);
        return Objects.equals(registered.getPermission(), permission);
    }

    private boolean internalReset(String command) {
        var registered = Bukkit.getCommandMap().getKnownCommands().get(command);
        if (registered == null) return false;
        if (!originalPermissions.containsKey(command)) return false;
        var permission = originalPermissions.remove(command);
        if (Objects.equals(registered.getPermission(), permission)) return false;
        registered.setPermission(permission);
        return Objects.equals(registered.getPermission(), permission);
    }
}
