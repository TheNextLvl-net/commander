package net.thenextlvl.commander.implementation;

import net.thenextlvl.commander.api.CommandManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class CraftCommandManager extends CommandManager {
    public CraftCommandManager(File dataFolder) {
        super(dataFolder);
    }

    @Override
    public void unregisterCommand(String label) {
        var name = resolveCommandName(label);
        if (name != null) Bukkit.getCommandMap().getKnownCommands().entrySet().stream()
                .filter(entry -> entry.getValue().getName().equals(name))
                .toList()
                .forEach(entry -> {
                    Bukkit.getCommandMap().getKnownCommands().remove(entry.getKey());
                    entry.getValue().unregister(Bukkit.getCommandMap());
                });
    }

    @Override
    public boolean isCommandRegistered(String command) {
        return Bukkit.getCommandMap().getKnownCommands().containsKey(command);
    }

    public @Nullable String resolveCommandName(String label) {
        var command = Bukkit.getCommandMap().getCommand(label);
        return command != null ? command.getName() : null;
    }
}
