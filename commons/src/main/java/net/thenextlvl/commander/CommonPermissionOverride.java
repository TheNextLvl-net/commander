package net.thenextlvl.commander;

import com.google.gson.reflect.TypeToken;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.util.FileUtil;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public abstract class CommonPermissionOverride implements PermissionOverride {
    protected final FileIO<Map<String, @Nullable String>> overridesFile;
    protected final CommanderCommons commons;

    protected String overridesDigest;
    protected long overridesLastModified;

    public CommonPermissionOverride(CommanderCommons commons) {
        this.overridesFile = new GsonFile<Map<String, @Nullable String>>(
                IO.of(commons.getDataPath().resolve("permission-overrides.json")),
                new HashMap<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.overridesDigest = FileUtil.digest(overridesFile);
        this.overridesLastModified = FileUtil.lastModified(overridesFile);
        this.commons = commons;

    }

    public boolean save(boolean force) {
        if (!force && FileUtil.hasChanged(overridesFile, overridesDigest, overridesLastModified)) return false;
        overridesFile.save();
        overridesDigest = FileUtil.digest(overridesFile);
        overridesLastModified = FileUtil.lastModified(overridesFile);
        return true;
    }

    public boolean reload(Audience audience) {
        var previous = overridesFile.getRoot();
        var current = overridesFile.reload();
        overridesDigest = FileUtil.digest(overridesFile);
        overridesLastModified = FileUtil.lastModified(overridesFile);
        if (previous.equals(current.getRoot())) return false;
        var difference = difference(previous, current.getRoot());
        var additions = difference.entrySet().stream()
                .filter(Map.Entry::getValue).count();
        commons.bundle().sendMessage(audience, "command.reload.changes",
                Placeholder.parsed("additions", String.valueOf(additions)),
                Placeholder.parsed("deletions", String.valueOf(difference.size() - additions)),
                Placeholder.parsed("file", "permission-overrides.json"));
        difference.forEach((command, added) -> {
            if (added) override(command.command(), command.permission());
            else reset(command.command());
        });
        return true;
    }

    @Override
    public @Unmodifiable Map<String, @Nullable String> overrides() {
        return Map.copyOf(overridesFile.getRoot());
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
        var commands = commons.commandFinder().findCommands(command).stream()
                .filter(s -> internalOverride(s, permission))
                .toList();
        if (commands.isEmpty()) return false;
        commands.forEach(s -> overridesFile.getRoot().put(s, permission));
        commons.updateCommands();
        return true;
    }

    @Override
    public boolean reset(String command) {
        var commands = commons.commandFinder().findCommands(overridesFile.getRoot().keySet().stream(), command);
        commands.forEach(overridesFile.getRoot()::remove);
        if (!commands.stream().map(this::internalReset).reduce(false, Boolean::logicalOr)) return false;
        commons.updateCommands();
        return true;
    }

    protected abstract boolean internalOverride(String command, @Nullable String permission);

    protected abstract boolean internalReset(String command);

    protected Map<PermissionOverride, Boolean> difference(Map<String, @Nullable String> previous, Map<String, @Nullable String> current) {
        var differences = new HashMap<PermissionOverride, Boolean>();
        current.entrySet().stream()
                .filter(entry -> !Objects.equals(previous.get(entry.getKey()), entry.getValue()))
                .forEach(entry -> differences.put(new PermissionOverride(entry.getKey(), entry.getValue()), true));
        previous.entrySet().stream()
                .filter(entry -> !current.containsKey(entry.getKey()))
                .forEach(entry -> differences.put(new PermissionOverride(entry.getKey(), entry.getValue()), false));
        return differences;
    }

    protected record PermissionOverride(String command, @Nullable String permission) {
    }
}
