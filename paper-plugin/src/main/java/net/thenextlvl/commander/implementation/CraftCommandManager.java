package net.thenextlvl.commander.implementation;

import net.thenextlvl.commander.api.CommandManager;
import org.bukkit.Bukkit;

import java.io.File;

public class CraftCommandManager extends CommandManager {
    public CraftCommandManager(File dataFolder) {
        super(dataFolder);
    }

    @Override
    public void unregisterCommand(String label) {
        var literal = Bukkit.getCommandMap().getKnownCommands().get(label);
        if (literal == null) return;

        Bukkit.getCommandMap().getKnownCommands().remove(label);
        literal.unregister(Bukkit.getCommandMap());

        var split = label.split(":", 2);
        if (split.length != 2) return;

        var command = Bukkit.getCommandMap().getKnownCommands().get(split[1]);
        if (command == null || !command.equals(literal)) return;

        Bukkit.getCommandMap().getKnownCommands().remove(split[1]);
        command.unregister(Bukkit.getCommandMap());
    }

    @Override
    public boolean isCommandRegistered(String command) {
        return Bukkit.getCommandMap().getKnownCommands().containsKey(command);
    }
}
