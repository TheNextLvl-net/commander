package net.thenextlvl.commander.paper.implementation;

import net.thenextlvl.commander.CommonCommandRegistry;
import net.thenextlvl.commander.paper.CommanderPlugin;
import org.bukkit.command.Command;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class PaperCommandRegistry extends CommonCommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();
    private final CommanderPlugin plugin;

    public PaperCommandRegistry(CommanderPlugin plugin) {
        super(plugin.commons);
        this.plugin = plugin;
    }


    @Override
    protected boolean internalRegister(String command) {
        var register = commands.remove(command);
        if (register == null) return false;
        plugin.getServer().getCommandMap().getKnownCommands().put(command, register);
        return true;
    }

    @Override
    protected boolean internalUnregister(String command) {
        var registered = plugin.getServer().getCommandMap().getKnownCommands().remove(command);
        if (registered == null) return false;
        commands.put(command, registered);
        return true;
    }
}
