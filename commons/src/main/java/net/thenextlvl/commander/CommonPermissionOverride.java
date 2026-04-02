package net.thenextlvl.commander;

import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.file.GsonFile;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@NullMarked
public abstract class CommonPermissionOverride implements PermissionOverride {
    protected final GsonFile<ConcurrentHashMap<String, String>> overridesFile;
    protected final CommanderCommons commons;

    public CommonPermissionOverride(final CommanderCommons commons) {
        this.overridesFile = new GsonFile<ConcurrentHashMap<String, String>>(
                commons.getDataPath().resolve("permission-overrides.json"),
                new ConcurrentHashMap<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.commons = commons;
    }

    public boolean save(final boolean force) {
        if (!force && overridesFile.hasChanged()) return false;
        overridesFile.save();
        return true;
    }

    public boolean reload(final Audience audience) {
        final var previous = overridesFile.getRoot();
        final var current = overridesFile.reload();
        if (previous.equals(current.getRoot())) return false;
        final var difference = difference(previous, current.getRoot());
        final var additions = difference.entrySet().stream()
                .filter(Map.Entry::getValue).count();
        commons.bundle().sendMessage(audience, "command.reload.changes",
                Formatter.number("additions", additions),
                Formatter.number("deletions", difference.size() - additions),
                Placeholder.parsed("file", "permission-overrides.json"));
        difference.forEach((command, added) -> {
            if (added) override(command.command(), command.permission());
            else reset(command.command());
        });
        return true;
    }

    @Override
    public @Unmodifiable Map<String, String> overrides() {
        return Map.copyOf(overridesFile.getRoot());
    }

    @Override
    public @Nullable String permission(final String command) {
        return overridesFile.getRoot().get(command);
    }

    @Override
    public boolean isOverridden(final String command) {
        return overridesFile.getRoot().containsKey(command);
    }

    @Override
    public boolean override(final String command, final String permission) {
        final var commands = commons.commandFinder().findCommands(command);
        if (!Stream.concat(commands, Stream.of(command))
                .filter(s -> internalOverride(s, permission))
                .map(s -> overridesFile.getRoot().put(s, permission) == null)
                .reduce(false, Boolean::logicalOr)) return false;
        commons.updateCommands();
        return true;
    }

    @Override
    public boolean reset(final String command) {
        final var commands = commons.commandFinder().findCommands(overridesFile.getRoot().keySet().stream(), command);
        final var reset = Stream.concat(commands, Stream.of(command)).toList();
        if (!reset.stream()
                .map(s -> overridesFile.getRoot().remove(s) != null)
                .reduce(false, Boolean::logicalOr)) return false;
        if (!reset.stream().map(this::internalReset).reduce(false, Boolean::logicalOr)) return false;
        commons.updateCommands();
        return true;
    }

    protected abstract boolean internalOverride(String command, String permission);

    protected abstract boolean internalReset(String command);

    protected Map<PermissionOverride, Boolean> difference(final Map<String, String> previous, final Map<String, String> current) {
        final var differences = new HashMap<PermissionOverride, Boolean>();
        current.entrySet().stream()
                .filter(entry -> !Objects.equals(previous.get(entry.getKey()), entry.getValue()))
                .forEach(entry -> differences.put(new PermissionOverride(entry.getKey(), entry.getValue()), true));
        previous.entrySet().stream()
                .filter(entry -> !current.containsKey(entry.getKey()))
                .forEach(entry -> differences.put(new PermissionOverride(entry.getKey(), entry.getValue()), false));
        return differences;
    }

    protected record PermissionOverride(String command, String permission) {
    }
}
