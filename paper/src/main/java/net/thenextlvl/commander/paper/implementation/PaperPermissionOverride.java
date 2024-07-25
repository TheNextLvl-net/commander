package net.thenextlvl.commander.paper.implementation;

import com.google.gson.reflect.TypeToken;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.PermissionOverride;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class PaperPermissionOverride implements PermissionOverride {
    private final Map<String, @Nullable String> originalPermissions = new HashMap<>();
    private final FileIO<Map<String, @Nullable String>> overridesFile;

    public PaperPermissionOverride(CommanderPlugin plugin) {
        this.overridesFile = new GsonFile<Map<String, @Nullable String>>(
                IO.of(plugin.getDataFolder(), "permission-overrides.json"),
                new HashMap<>(), new TypeToken<>() {
        }).saveIfAbsent();
    }

    @Override
    public Map<String, @Nullable String> overrides() {
        return Map.copyOf(overridesFile.getRoot());
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
        overridesFile.getRoot().put(command, permission);
        return internalOverride(command, permission);
    }

    @Override
    public boolean reset(String command) {
        if (!overridesFile.getRoot().containsKey(command)) return false;
        overridesFile.getRoot().remove(command);
        return internalReset(command);
    }

    @Override
    public void overridePermissions() {
        overridesFile.getRoot().forEach(this::internalOverride);
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
