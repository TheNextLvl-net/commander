package net.thenextlvl.commander.implementation;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.thenextlvl.commander.api.CommandRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Accessors(fluent = true)
public class PaperCommandRegistry implements CommandRegistry {
    private final @Getter Set<String> hiddenCommands = new HashSet<>();
    private final @Getter Set<String> unregisteredCommands = new HashSet<>();
    private final Map<String, Command> commands = new HashMap<>();

    @Override
    public boolean hide(String command) {
        return hiddenCommands.add(command);
    }

    @Override
    public boolean isHidden(String command) {
        return hiddenCommands.contains(command);
    }

    @Override
    public boolean isUnregistered(String command) {
        return unregisteredCommands.contains(command);
    }

    @Override
    public boolean register(String command) {
        return unregisteredCommands.remove(command) && internalRegister(command);
    }

    @Override
    public boolean reveal(String command) {
        return hiddenCommands.remove(command);
    }

    @Override
    public boolean unregister(String command) {
        return unregisteredCommands.add(command) && internalUnregister(command);
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
