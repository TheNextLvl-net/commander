package net.thenextlvl.commander.paper.implementation;

import com.google.gson.reflect.TypeToken;
import core.file.FileIO;
import core.file.format.GsonFile;
import core.io.IO;
import lombok.Getter;
import net.thenextlvl.commander.paper.CommanderPlugin;
import net.thenextlvl.commander.CommandRegistry;
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

    public PaperCommandRegistry(CommanderPlugin plugin) {
        this.hiddenFile = new GsonFile<Set<String>>(
                IO.of(plugin.getDataFolder(), "hidden-commands.json"),
                new HashSet<>(), new TypeToken<>() {
        }).saveIfAbsent();
        this.unregisteredFile = new GsonFile<Set<String>>(
                IO.of(plugin.getDataFolder(), "removed-commands.json"),
                new HashSet<>(), new TypeToken<>() {
        }).saveIfAbsent();
    }

    @Override
    public Set<String> hiddenCommands() {
        return Set.copyOf(hiddenFile.getRoot());
    }

    @Override
    public Set<String> unregisteredCommands() {
        return Set.copyOf(unregisteredFile.getRoot());
    }

    @Override
    public boolean hide(String command) {
        return Bukkit.getCommandMap().getKnownCommands().containsKey(command)
               && hiddenFile.getRoot().add(command);
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
        return unregisteredFile.getRoot().remove(command) && internalRegister(command);
    }

    @Override
    public boolean reveal(String command) {
        return hiddenFile.getRoot().remove(command);
    }

    @Override
    public boolean unregister(String command) {
        return unregisteredFile.getRoot().add(command) && internalUnregister(command);
    }

    @Override
    public void unregisterCommands() {
        unregisteredCommands().forEach(this::internalUnregister);
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
