package net.thenextlvl.commander.velocity.implementation;

import com.google.gson.reflect.TypeToken;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import lombok.Getter;
import net.thenextlvl.commander.CommandRegistry;
import net.thenextlvl.commander.velocity.CommanderPlugin;

import java.util.HashSet;
import java.util.Set;

@Getter
public class ProxyCommandRegistry implements CommandRegistry {
    private final FileIO<Set<String>> hiddenFile;
    private final FileIO<Set<String>> unregisteredFile;
    private final CommanderPlugin plugin;

    public ProxyCommandRegistry(CommanderPlugin plugin) {
        this.hiddenFile = new GsonFile<Set<String>>(
                IO.of(plugin.dataFolder().toFile(), "hidden-commands.json"),
                new HashSet<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.unregisteredFile = new GsonFile<Set<String>>(
                IO.of(plugin.dataFolder().toFile(), "removed-commands.json"),
                new HashSet<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.plugin = plugin;
    }

    @Override
    public Set<String> hiddenCommands() {
        return new HashSet<>(hiddenFile.getRoot());
    }

    @Override
    public Set<String> unregisteredCommands() {
        return new HashSet<>(unregisteredFile.getRoot());
    }

    @Override
    public boolean hide(String command) {
        return !plugin.commandFinder().findCommands(command).stream()
                .filter(hiddenFile.getRoot()::add)
                .toList().isEmpty();
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
        return !plugin.commandFinder().findCommands(new HashSet<>(unregisteredFile.getRoot()).stream(), command).stream()
                .filter(unregisteredFile.getRoot()::remove)
                .toList().isEmpty();
    }

    @Override
    public boolean reveal(String command) {
        return !plugin.commandFinder().findCommands(new HashSet<>(hiddenFile.getRoot()).stream(), command).stream()
                .filter(hiddenFile.getRoot()::remove)
                .toList().isEmpty();
    }

    @Override
    public boolean unregister(String command) {
        return !plugin.commandFinder().findCommands(command).stream()
                .filter(s -> !s.equals("commandv"))
                .filter(plugin.server().getCommandManager()::hasCommand)
                .filter(unregisteredFile.getRoot()::add)
                .filter(this::internalUnregister)
                .toList().isEmpty();
    }

    @Override
    public void unregisterCommands() {
        unregisteredCommands().forEach(this::internalUnregister);
    }

    private boolean internalUnregister(String command) {
        plugin.server().getCommandManager().unregister(command);
        return true;
    }
}
