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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class PaperPermissionOverride implements PermissionOverride {
    private final Map<String, @Nullable String> originalPermissions = new HashMap<>();
    private final FileIO<Map<String, @Nullable String>> overridesFile;
    private final CommanderPlugin plugin;

    public PaperPermissionOverride(CommanderPlugin plugin) {
        this.overridesFile = new GsonFile<Map<String, @Nullable String>>(
                IO.of(plugin.getDataFolder(), "permission-overrides.json"),
                new HashMap<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.plugin = plugin;
    }

    @Override
    public Map<String, @Nullable String> overrides() {
        return new HashMap<>(overridesFile.getRoot());
    }

    @Override
    public Map<String, @Nullable String> originalPermissions() {
        return new HashMap<>(originalPermissions);
    }

    @Override
    public @Nullable String originalPermission(String command) {
        return originalPermissions.get(command);
    }

    @Override
    public String permission(String command) {
        return overridesFile.getRoot().get(command);
    }

    @Override
    public boolean isOverridden(String command) {
        return overridesFile.getRoot().containsKey(command);
    }

    @Override
    public boolean override(String command, @Nullable String permission) {
        var commands = plugin.commandFinder().findCommands(command).stream()
                .filter(s -> internalOverride(s, permission))
                .toList();
        commands.forEach(s -> overridesFile.getRoot().put(s, permission));
        return !commands.isEmpty();
    }

    @Override
    public boolean reset(String command) {
        var commands = plugin.commandFinder().findCommands(new HashSet<>(overridesFile.getRoot().keySet()).stream(), command);
        commands.forEach(overridesFile.getRoot()::remove);
        return !commands.stream()
                .filter(this::internalReset)
                .toList().isEmpty();
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
