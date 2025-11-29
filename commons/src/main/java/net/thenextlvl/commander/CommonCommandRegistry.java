package net.thenextlvl.commander;

import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.file.GsonFile;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public abstract class CommonCommandRegistry implements CommandRegistry {
    protected final GsonFile<Set<String>> hiddenFile;
    protected final GsonFile<Set<String>> unregisteredFile;
    protected final CommanderCommons commons;

    protected CommonCommandRegistry(CommanderCommons commons) {
        this.hiddenFile = new GsonFile<Set<String>>(
                commons.getDataPath().resolve("hidden-commands.json"),
                new HashSet<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.unregisteredFile = new GsonFile<Set<String>>(
                commons.getDataPath().resolve("removed-commands.json"),
                new HashSet<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.commons = commons;
    }

    @Override
    public @Unmodifiable Set<String> hiddenCommands() {
        return Set.copyOf(hiddenFile.getRoot());
    }

    @Override
    public @Unmodifiable Set<String> unregisteredCommands() {
        return Set.copyOf(unregisteredFile.getRoot());
    }

    @Override
    public boolean hide(String command) {
        var commands = commons.commandFinder().findCommands(command);
        if (!Stream.concat(commands, Stream.of(command))
                .map(hiddenFile.getRoot()::add)
                .reduce(false, Boolean::logicalOr)) return false;
        commons.updateCommands();
        return true;
    }

    @Override
    public boolean isHidden(String command) {
        return hiddenFile.getRoot().contains(command);
    }

    @Override
    public boolean isUnregistered(String command) {
        return unregisteredFile.getRoot().contains(command);
    }

    @Override
    public boolean register(String command) {
        if (!commons.commandFinder().findCommands(Set.copyOf(unregisteredFile.getRoot()).stream(), command)
                .filter(unregisteredFile.getRoot()::remove)
                .map(this::internalRegister)
                .reduce(false, Boolean::logicalOr)) return false;
        commons.updateCommands();
        return true;
    }

    @Override
    public boolean reveal(String command) {
        if (!commons.commandFinder().findCommands(Set.copyOf(hiddenFile.getRoot()).stream(), command)
                .map(hiddenFile.getRoot()::remove)
                .reduce(false, Boolean::logicalOr)) return false;
        commons.updateCommands();
        return true;
    }

    @Override
    public void unregisterCommands() {
        unregisteredCommands().stream()
                .filter(command -> !command.equals(commons.getRootCommand()))
                .forEach(this::internalUnregister);
    }

    @Override
    public boolean unregister(String command) {
        if (!commons.commandFinder().findCommands(command)
                .filter(s -> !s.equals(commons.getRootCommand()))
                .filter(unregisteredFile.getRoot()::add)
                .map(this::internalUnregister)
                .reduce(false, Boolean::logicalOr)) return false;
        commons.updateCommands();
        return true;
    }

    public boolean save(boolean force) {
        return saveHidden(force) & saveUnregistered(force);
    }

    public boolean saveHidden(boolean force) {
        if (!force && hiddenFile.hasChanged()) return false;
        hiddenFile.save();
        return true;
    }

    public boolean saveUnregistered(boolean force) {
        if (!force && unregisteredFile.hasChanged()) return false;
        unregisteredFile.save();
        return true;
    }

    public boolean reload(Audience audience) {
        return reloadHidden(audience) | reloadUnregistered(audience);
    }

    private boolean reloadHidden(Audience audience) {
        var previous = hiddenFile.getRoot();
        var current = hiddenFile.reload();
        if (previous.equals(current.getRoot())) return false;
        var difference = difference(previous, current.getRoot());
        var additions = difference.entrySet().stream()
                .filter(Map.Entry::getValue).count();
        commons.bundle().sendMessage(audience, "command.reload.changes",
                Formatter.number("additions", additions),
                Formatter.number("deletions", difference.size() - additions),
                Placeholder.parsed("file", "hidden-commands.json"));
        commons.updateCommands();
        return true;
    }

    private boolean reloadUnregistered(Audience audience) {
        var previous = unregisteredFile.getRoot();
        var current = unregisteredFile.reload();
        if (previous.equals(current.getRoot())) return false;
        var difference = difference(previous, current.getRoot());
        var additions = difference.entrySet().stream()
                .filter(Map.Entry::getValue).count();
        difference.forEach((command, added) -> {
            if (added) internalUnregister(command);
            else internalRegister(command);
        });
        commons.bundle().sendMessage(audience, "command.reload.changes",
                Formatter.number("additions", additions),
                Formatter.number("deletions", difference.size() - additions),
                Placeholder.parsed("file", "unregistered-commands.json"));
        commons.updateCommands();
        return true;
    }

    protected abstract boolean internalRegister(String command);

    protected abstract boolean internalUnregister(String command);

    private Map<String, Boolean> difference(Set<String> previous, Set<String> current) {
        var differences = new HashMap<String, Boolean>();
        differences.putAll(current.stream()
                .filter(s -> !previous.contains(s))
                .collect(Collectors.toMap(s -> s, s -> true)));
        differences.putAll(previous.stream()
                .filter(s -> !current.contains(s))
                .collect(Collectors.toMap(s -> s, s -> false)));
        return differences;
    }
}
