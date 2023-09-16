package net.thenextlvl.commander.implementation.command;

import net.thenextlvl.commander.api.command.PlatformCommandRegistry;
import net.thenextlvl.commander.implementation.CraftCommander;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public record CraftPlatformCommandRegistry(CraftCommander commander) implements PlatformCommandRegistry<Command> {

    @Override
    public Stream<String> getCommandNamespaces() {
        return Stream.concat(
                Bukkit.getCommandMap().getKnownCommands().keySet().stream(),
                getCommands().stream().map(Command::getLabel)
        );
    }

    @Override
    public Collection<Command> getCommands() {
        return Bukkit.getCommandMap().getKnownCommands().values();
    }

    @Override
    public Optional<Command> getCommand(String literal) {
        return Optional.ofNullable(Bukkit.getCommandMap().getCommand(literal));
    }

    @Override
    public boolean matches(Command first, Command second) {
        if (first.equals(second)) return true;
        if (!first.getClass().equals(second.getClass())) return false;
        if (!first.getName().equals(second.getName())) return false;
        return first.getLabel().equals(second.getLabel());
    }

    @Override
    public void updateCommands() {
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }
}
