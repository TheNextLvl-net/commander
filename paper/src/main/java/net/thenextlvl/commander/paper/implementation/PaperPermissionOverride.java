package net.thenextlvl.commander.paper.implementation;

import net.thenextlvl.commander.CommonPermissionOverride;
import net.thenextlvl.commander.paper.PaperCommander;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class PaperPermissionOverride extends CommonPermissionOverride {
    private final Map<String, @Nullable String> originalPermissions = new HashMap<>();

    public PaperPermissionOverride(PaperCommander commander) {
        super(commander);
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
    protected boolean internalOverride(String command, String permission) {
        var plugin = ((PaperCommander) commons).getPlugin();
        var registered = plugin.getServer().getCommandMap().getKnownCommands().get(command);

        var registeredPermission = registered != null ? registered.getPermission() : null;
        if (permission.equals(registeredPermission)) return false;
        originalPermissions.putIfAbsent(command, registeredPermission);

        var dispatcher = plugin.commandDispatcher();
        var child = dispatcher != null ? dispatcher.getRoot().getChild(command) : null;
        if (child != null) return true;

        if (registered == null) return false;
        registered.setPermission(permission);
        return Objects.equals(registered.getPermission(), permission);
    }

    @Override
    protected boolean internalReset(String command) {
        var plugin = ((PaperCommander) commons).getPlugin();
        var registered = plugin.getServer().getCommandMap().getKnownCommands().get(command);

        if (!originalPermissions.containsKey(command)) return false;
        var permission = originalPermissions.remove(command);

        var dispatcher = plugin.commandDispatcher();
        var child = dispatcher != null ? dispatcher.getRoot().getChild(command) : null;
        if (child != null) return true;

        if (registered == null) return false;
        if (Objects.equals(registered.getPermission(), permission)) return false;
        registered.setPermission(permission);
        return Objects.equals(registered.getPermission(), permission);
    }
}
