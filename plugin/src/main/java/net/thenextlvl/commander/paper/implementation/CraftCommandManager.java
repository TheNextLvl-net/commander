package net.thenextlvl.commander.paper.implementation;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.commander.api.CommandInfo;
import net.thenextlvl.commander.api.platform.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public record CraftCommandManager(CraftCommander commander) implements CommandManager<Command> {

    @Override
    public Stream<String> getCommandNames() {
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
    public Stream<Command> getCommands(String query) {
        return Bukkit.getCommandMap().getKnownCommands().keySet().stream()
                .filter(command -> CommandInfo.nameMatches(command, query))
                .map(s -> getCommand(s).orElse(null))
                .filter(Objects::nonNull);
    }

    @Override
    public Optional<Command> getCommand(String literal) {
        return Optional.ofNullable(Bukkit.getCommandMap().getCommand(literal));
    }

    @Override
    public void updateCommands() {
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }
}
