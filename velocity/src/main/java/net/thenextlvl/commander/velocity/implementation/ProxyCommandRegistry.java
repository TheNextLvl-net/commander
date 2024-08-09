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
        }).saveIfAbsent();
        this.unregisteredFile = new GsonFile<Set<String>>(
                IO.of(plugin.dataFolder().toFile(), "removed-commands.json"),
                new HashSet<>(), new TypeToken<>() {
        }).saveIfAbsent();
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
        return hiddenFile.getRoot().add(command);
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
        return unregisteredFile.getRoot().remove(command);
    }

    @Override
    public boolean reveal(String command) {
        return hiddenFile.getRoot().remove(command);
    }

    @Override
    public boolean unregister(String command) {
        if (!plugin.server().getCommandManager().hasCommand(command)) return false;
        return unregisteredFile.getRoot().add(command) && internalUnregister(command);
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
