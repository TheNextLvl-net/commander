package net.thenextlvl.commander.velocity.implementation;

import com.google.gson.reflect.TypeToken;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.PermissionOverride;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

@Getter
@NullMarked
@RequiredArgsConstructor
public class ProxyPermissionOverride implements PermissionOverride {
    private final FileIO<Map<String, @Nullable String>> overridesFile;
    private final CommanderPlugin plugin;

    public ProxyPermissionOverride(CommanderPlugin plugin) {
        this.overridesFile = new GsonFile<Map<String, @Nullable String>>(
                IO.of(plugin.dataFolder().toFile(), "permission-overrides.json"),
                new HashMap<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.plugin = plugin;
    }

    @Override
    public Map<String, @Nullable String> overrides() {
        return new HashMap<>(overridesFile.getRoot());
    }

    @Override
    @Deprecated
    public Map<String, @Nullable String> originalPermissions() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public @Nullable String originalPermission(String command) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
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
        return !plugin.commandFinder().findCommands(command).stream()
                .filter(s -> !Objects.equals(overridesFile.getRoot().put(s, permission), permission))
                .toList().isEmpty();
    }

    @Override
    public boolean reset(String command) {
        var overridden = new HashSet<>(overridesFile.getRoot().keySet()).stream();
        var commands = plugin.commandFinder().findCommands(overridden, command);
        return !commands.stream()
                .filter(this::isOverridden)
                .map(overridesFile.getRoot()::remove)
                .toList().isEmpty();
    }

    @Override
    @Deprecated
    public void overridePermissions() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
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
}
