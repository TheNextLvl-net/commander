package net.thenextlvl.commander.paper.implementation;

import com.google.gson.reflect.TypeToken;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import lombok.Getter;
import net.thenextlvl.commander.CommandRegistry;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class PaperCommandRegistry implements CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();
    private final FileIO<Set<String>> hiddenFile;
    private final FileIO<Set<String>> unregisteredFile;
    private final CommanderPlugin plugin;

    public PaperCommandRegistry(CommanderPlugin plugin) {
        this.hiddenFile = new GsonFile<Set<String>>(
                IO.of(plugin.getDataFolder(), "hidden-commands.json"),
                new HashSet<>(), new TypeToken<>() {
        }).reload().saveIfAbsent();
        this.unregisteredFile = new GsonFile<Set<String>>(
                IO.of(plugin.getDataFolder(), "removed-commands.json"),
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
        return !plugin.commandFinder().findCommands(new HashSet<>(commands.keySet()).stream(), command).stream()
                .filter(unregisteredFile.getRoot()::remove)
                .filter(this::internalRegister)
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
                .filter(s -> !s.equals("commander:command"))
                .filter(unregisteredFile.getRoot()::add)
                .filter(this::internalUnregister)
                .toList().isEmpty();
    }

    @Override
    public void unregisterCommands() {
        unregisteredCommands().stream()
                .filter(command -> !command.equals("commander:command"))
                .forEach(this::internalUnregister);
    }

    private boolean internalRegister(String command) {
        var register = commands.remove(command);
        if (register == null) return false;
        Bukkit.getCommandMap().getKnownCommands().put(command, register);
        return true;
    }

    private boolean internalUnregister(String command) {
        var registered = Bukkit.getCommandMap().getKnownCommands().remove(command);
        if (registered == null) return false;
        commands.put(command, registered);
        return true;
    }
}
