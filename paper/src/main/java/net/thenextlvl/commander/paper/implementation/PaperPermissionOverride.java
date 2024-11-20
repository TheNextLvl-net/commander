package net.thenextlvl.commander.paper.implementation;

import com.google.gson.reflect.TypeToken;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.PermissionOverride;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

@Getter
@NullMarked
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
    public @Nullable String permission(String command) {
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

    @Override
    public boolean reload(Audience audience) {
        var previous = getOverridesFile().getRoot();
        var current = getOverridesFile().reload();
        if (previous.equals(current.getRoot())) return false;
        var difference = difference(previous, current.getRoot());
        var additions = difference.entrySet().stream()
                .filter(Map.Entry::getValue).count();
        plugin.bundle().sendMessage(audience, "command.reload.changes",
                Placeholder.parsed("additions", String.valueOf(additions)),
                Placeholder.parsed("deletions", String.valueOf(difference.size() - additions)),
                Placeholder.parsed("file", "permission-overrides.json"));
        difference.forEach((command, added) -> {
            if (added) override(command.command(), command.permission());
            else reset(command.command());
        });
        return true;
    }

    private Map<PermissionOverride, Boolean> difference(Map<String, @Nullable String> previous, Map<String, @Nullable String> current) {
        var differences = new HashMap<PermissionOverride, Boolean>();
        current.entrySet().stream()
                .filter(entry -> !Objects.equals(previous.get(entry.getKey()), entry.getValue()))
                .forEach(entry -> differences.put(new PermissionOverride(entry.getKey(), entry.getValue()), true));
        previous.entrySet().stream()
                .filter(entry -> !current.containsKey(entry.getKey()))
                .forEach(entry -> differences.put(new PermissionOverride(entry.getKey(), entry.getValue()), false));
        return differences;
    }

    private record PermissionOverride(String command, @Nullable String permission) {
    }

    private boolean internalOverride(String command, @Nullable String permission) {
        var registered = plugin.getServer().getCommandMap().getKnownCommands().get(command);
        if (registered == null) return false;
        if (Objects.equals(registered.getPermission(), permission)) return false;
        originalPermissions.putIfAbsent(command, registered.getPermission());
        registered.setPermission(permission);
        return Objects.equals(registered.getPermission(), permission);
    }

    private boolean internalReset(String command) {
        var registered = plugin.getServer().getCommandMap().getKnownCommands().get(command);
        if (registered == null) return false;
        if (!originalPermissions.containsKey(command)) return false;
        var permission = originalPermissions.remove(command);
        if (Objects.equals(registered.getPermission(), permission)) return false;
        registered.setPermission(permission);
        return Objects.equals(registered.getPermission(), permission);
    }
}
