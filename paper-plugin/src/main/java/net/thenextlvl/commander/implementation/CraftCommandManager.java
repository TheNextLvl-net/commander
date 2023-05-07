package net.thenextlvl.commander.implementation;

import net.thenextlvl.commander.api.CommandManager;
import org.bukkit.Bukkit;

import java.io.File;

public class CraftCommandManager extends CommandManager {
    public CraftCommandManager(File dataFolder) {
        super(dataFolder);
    }

    @Override
    public void unregisterCommand(String label, boolean alias) {
        var command = Bukkit.getCommandMap().getCommand(label);
        if (command == null) return;
        if (!alias) command.getAliases().forEach(s -> unregisterCommand(s, true));
        Bukkit.getCommandMap().getKnownCommands().remove(label);
        command.unregister(Bukkit.getCommandMap());
    }
}
