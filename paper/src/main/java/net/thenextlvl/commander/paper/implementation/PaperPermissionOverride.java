package net.thenextlvl.commander.paper.implementation;

import net.thenextlvl.commander.CommonPermissionOverride;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class PaperPermissionOverride extends CommonPermissionOverride {
    private final Map<String, @Nullable String> originalPermissions = new HashMap<>();
    private final CommanderPlugin plugin;

    public PaperPermissionOverride(CommanderPlugin plugin) {
        super(plugin.commons);
        this.plugin = plugin;
    }

    @Override
    public @Unmodifiable Map<String, @Nullable String> originalPermissions() {
        return Map.copyOf(originalPermissions);
    }

    @Override
    public @Nullable String originalPermission(String command) {
        return originalPermissions.get(command);
    }

    @Override
    public void overridePermissions() {
        overridesFile.getRoot().forEach(this::internalOverride);
    }

    @Override
    protected boolean internalOverride(String command, @Nullable String permission) {
        var registered = plugin.getServer().getCommandMap().getKnownCommands().get(command);
        if (registered == null) return false;
        var registeredPermission = registered.getPermission();
        if (Objects.equals(registeredPermission, permission)) return false;
        originalPermissions.putIfAbsent(command, registeredPermission);
        registered.setPermission(permission);
        return true;
    }

    @Override
    protected boolean internalReset(String command) {
        var registered = plugin.getServer().getCommandMap().getKnownCommands().get(command);
        if (registered == null) return false;
        if (!originalPermissions.containsKey(command)) return false;
        var permission = originalPermissions.remove(command);
        if (Objects.equals(registered.getPermission(), permission)) return false;
        registered.setPermission(permission);
        return Objects.equals(registered.getPermission(), permission);
    }
}
