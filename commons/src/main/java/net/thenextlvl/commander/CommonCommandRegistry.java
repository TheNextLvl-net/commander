package net.thenextlvl.commander;

import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.file.GsonFile;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public abstract class CommonCommandRegistry implements CommandRegistry {
    protected final GsonFile<CopyOnWriteArraySet<String>> hiddenFile;
    protected final GsonFile<CopyOnWriteArraySet<String>> unregisteredFile;
    protected final CommanderCommons commons;

    protected CommonCommandRegistry(final CommanderCommons commons) {
        this.hiddenFile = new GsonFile<CopyOnWriteArraySet<String>>(
                commons.getDataPath().resolve("hidden-commands.json"),
                new CopyOnWriteArraySet<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.unregisteredFile = new GsonFile<CopyOnWriteArraySet<String>>(
                commons.getDataPath().resolve("removed-commands.json"),
                new CopyOnWriteArraySet<>(), new TypeToken<>() {
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
    public boolean hide(final String command) {
        final var commands = commons.commandFinder().findCommands(command);
        if (!Stream.concat(commands, Stream.of(command))
                .map(hiddenFile.getRoot()::add)
                .reduce(false, Boolean::logicalOr)) return false;
        commons.updateCommands();
        return true;
    }

    @Override
    public boolean isHidden(final String command) {
        return hiddenFile.getRoot().contains(command);
    }

    @Override
    public boolean isUnregistered(final String command) {
        return unregisteredFile.getRoot().contains(command);
    }

    @Override
    public boolean register(final String command) {
        if (!commons.commandFinder().findCommands(Set.copyOf(unregisteredFile.getRoot()).stream(), command)
                .filter(unregisteredFile.getRoot()::remove)
                .map(this::internalRegister)
                .reduce(false, Boolean::logicalOr)) return false;
        commons.updateCommands();
        return true;
    }

    @Override
    public boolean reveal(final String command) {
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
    public boolean unregister(final String command) {
        if (!commons.commandFinder().findCommands(command)
                .filter(s -> !s.equals(commons.getRootCommand()))
                .filter(unregisteredFile.getRoot()::add)
                .map(this::internalUnregister)
                .reduce(false, Boolean::logicalOr)) return false;
        commons.updateCommands();
        return true;
    }

    public boolean save(final boolean force) {
        return saveHidden(force) & saveUnregistered(force);
    }

    public boolean saveHidden(final boolean force) {
        if (!force && hiddenFile.hasChanged()) return false;
        hiddenFile.save();
        return true;
    }

    public boolean saveUnregistered(final boolean force) {
        if (!force && unregisteredFile.hasChanged()) return false;
        unregisteredFile.save();
        return true;
    }

    public boolean reload(final Audience audience) {
        return reloadHidden(audience) | reloadUnregistered(audience);
    }

    private boolean reloadHidden(final Audience audience) {
        final var previous = hiddenFile.getRoot();
        final var current = hiddenFile.reload();
        if (previous.equals(current.getRoot())) return false;
        final var difference = difference(previous, current.getRoot());
        final var additions = difference.entrySet().stream()
                .filter(Map.Entry::getValue).count();
        commons.bundle().sendMessage(audience, "command.reload.changes",
                Formatter.number("additions", additions),
                Formatter.number("deletions", difference.size() - additions),
                Placeholder.parsed("file", "hidden-commands.json"));
        commons.updateCommands();
        return true;
    }

    private boolean reloadUnregistered(final Audience audience) {
        final var previous = unregisteredFile.getRoot();
        final var current = unregisteredFile.reload();
        if (previous.equals(current.getRoot())) return false;
        final var difference = difference(previous, current.getRoot());
        final var additions = difference.entrySet().stream()
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

    private Map<String, Boolean> difference(final Set<String> previous, final Set<String> current) {
        final var differences = new HashMap<String, Boolean>();
        differences.putAll(current.stream()
                .filter(s -> !previous.contains(s))
                .collect(Collectors.toMap(s -> s, s -> true)));
        differences.putAll(previous.stream()
                .filter(s -> !current.contains(s))
                .collect(Collectors.toMap(s -> s, s -> false)));
        return differences;
    }
}
