package net.thenextlvl.commander.implementation.command;

import net.thenextlvl.commander.api.command.PlatformCommandRegistry;
import net.thenextlvl.commander.implementation.CraftCommander;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.util.Collection;

public record CraftPlatformCommandRegistry(CraftCommander commander) implements PlatformCommandRegistry<Command> {

    @Override
    public boolean unregisterCommand(Command command) {
        getCommands().removeIf(command::equals);
        return command.unregister(Bukkit.getCommandMap());
    }

    @Override
    public Collection<Command> getCommands() {
        return Bukkit.getCommandMap().getKnownCommands().values();
    }

    @Override
    public Collection<Command> getCommands(String pattern) {
        return getCommands().stream()
                .filter(command -> command.getName().matches(pattern))
                .toList();
    }

    @Override
    public Command getCommand(String literal) {
        return Bukkit.getCommandMap().getCommand(literal);
    }

    @Override
    public boolean matches(Command first, Command second) {
        if (first.equals(second)) return true;
        if (!first.getClass().equals(second.getClass())) return false;
        if (!first.getName().equals(second.getName())) return false;
        return first.getLabel().equals(second.getLabel());
    }
}
