package net.thenextlvl.commander.paper.implementation;

import net.thenextlvl.commander.CommonCommandRegistry;
import net.thenextlvl.commander.paper.PaperCommander;
import org.bukkit.command.Command;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public class PaperCommandRegistry extends CommonCommandRegistry {
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public PaperCommandRegistry(final PaperCommander commons) {
        super(commons);
    }

    @Override
    protected boolean internalRegister(final String command) {
        final var register = commands.remove(command);
        if (register == null) return false;
        ((PaperCommander) commons).getServer().getCommandMap().getKnownCommands().put(command, register);
        return true;
    }

    @Override
    protected boolean internalUnregister(final String command) {
        final var registered = ((PaperCommander) commons).getServer().getCommandMap().getKnownCommands().remove(command);
        if (registered == null) return false;
        commands.put(command, registered);
        return true;
    }
}
