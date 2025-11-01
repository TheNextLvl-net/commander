package net.thenextlvl.commander;

import com.google.gson.reflect.TypeToken;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.commander.util.FileUtil;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public abstract class CommonCommandRegistry implements CommandRegistry {
    protected final FileIO<Set<String>> hiddenFile;
    protected final FileIO<Set<String>> unregisteredFile;
    protected final CommanderCommons commons;

    protected String hiddenDigest;
    protected String unregisteredDigest;
    protected long hiddenLastModified;
    protected long unregisteredLastModified;

    protected CommonCommandRegistry(CommanderCommons commons) {
        this.hiddenFile = new GsonFile<Set<String>>(
                IO.of(commons.getDataPath().resolve("hidden-commands.json")),
                new HashSet<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.unregisteredFile = new GsonFile<Set<String>>(
                IO.of(commons.getDataPath().resolve("removed-commands.json")),
                new HashSet<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.commons = commons;
        this.hiddenDigest = FileUtil.digest(hiddenFile);
        this.unregisteredDigest = FileUtil.digest(unregisteredFile);
        this.hiddenLastModified = FileUtil.lastModified(hiddenFile);
        this.unregisteredLastModified = FileUtil.lastModified(unregisteredFile);
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
        return commons.commandFinder().findCommands(command).stream()
                .map(hiddenFile.getRoot()::add)
                .reduce(false, Boolean::logicalOr);
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
        return reduce(command, unregisteredFile.getRoot());
    }

    @Override
    public boolean reveal(String command) {
        return reduce(command, hiddenFile.getRoot());
    }

    private boolean reduce(String command, Set<String> commands) {
        if (!commons.commandFinder().findCommands(commands.stream(), command).stream()
                .filter(commands::remove)
                .map(this::internalRegister)
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
        return commons.commandFinder().findCommands(command).stream()
                .filter(s -> !s.equals(commons.getRootCommand()))
                .filter(unregisteredFile.getRoot()::add)
                .map(this::internalUnregister)
                .reduce(false, Boolean::logicalOr);
    }

    public boolean save(boolean force) {
        return saveHidden(force) & saveUnregistered(force);
    }

    public boolean saveHidden(boolean force) {
        if (!force && FileUtil.hasChanged(hiddenFile, hiddenDigest, hiddenLastModified)) return false;
        hiddenFile.save();
        hiddenDigest = FileUtil.digest(hiddenFile);
        hiddenLastModified = FileUtil.lastModified(hiddenFile);
        return true;
    }

    public boolean saveUnregistered(boolean force) {
        if (!force && FileUtil.hasChanged(unregisteredFile, unregisteredDigest, unregisteredLastModified)) return false;
        unregisteredFile.save();
        unregisteredDigest = FileUtil.digest(unregisteredFile);
        unregisteredLastModified = FileUtil.lastModified(unregisteredFile);
        return true;
    }

    public boolean reload(Audience audience) {
        return reloadHidden(audience) | reloadUnregistered(audience);
    }

    private boolean reloadHidden(Audience audience) {
        var previous = hiddenFile.getRoot();
        var current = hiddenFile.reload();
        hiddenDigest = FileUtil.digest(hiddenFile);
        hiddenLastModified = FileUtil.lastModified(hiddenFile);
        if (previous.equals(current.getRoot())) return false;
        var difference = difference(previous, current.getRoot());
        var additions = difference.entrySet().stream()
                .filter(Map.Entry::getValue).count();
        commons.bundle().sendMessage(audience, "command.reload.changes",
                Formatter.number("additions", additions),
                Formatter.number("deletions", difference.size() - additions),
                Placeholder.parsed("file", "hidden-commands.json"));
        return true;
    }

    private boolean reloadUnregistered(Audience audience) {
        var previous = unregisteredFile.getRoot();
        var current = unregisteredFile.reload();
        unregisteredDigest = FileUtil.digest(unregisteredFile);
        unregisteredLastModified = FileUtil.lastModified(unregisteredFile);
        if (previous.equals(current.getRoot())) return false;
        var difference = difference(previous, current.getRoot());
        var additions = difference.entrySet().stream()
                .filter(Map.Entry::getValue).count();
        commons.bundle().sendMessage(audience, "command.reload.changes",
                Formatter.number("additions", additions),
                Formatter.number("deletions", difference.size() - additions),
                Placeholder.parsed("file", "unregistered-commands.json"));
        difference.forEach((command, added) -> {
            if (added) internalUnregister(command);
            else internalRegister(command);
        });
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
