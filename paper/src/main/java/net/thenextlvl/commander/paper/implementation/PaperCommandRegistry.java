package net.thenextlvl.commander.paper.implementation;

import net.thenextlvl.commander.CommonCommandRegistry;
import net.thenextlvl.commander.paper.PaperCommander;
import org.bukkit.command.Command;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class PaperCommandRegistry extends CommonCommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public PaperCommandRegistry(PaperCommander commons) {
        super(commons);
    }

    @Override
    protected boolean internalRegister(String command) {
        var register = commands.remove(command);
        if (register == null) return false;
        ((PaperCommander) commons).getServer().getCommandMap().getKnownCommands().put(command, register);
        return true;
    }

    @Override
    protected boolean internalUnregister(String command) {
        var registered = ((PaperCommander) commons).getServer().getCommandMap().getKnownCommands().remove(command);
        if (registered == null) return false;
        commands.put(command, registered);
        return true;
    }
}
