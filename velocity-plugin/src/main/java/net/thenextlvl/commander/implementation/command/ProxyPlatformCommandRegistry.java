package net.thenextlvl.commander.implementation.command;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import net.thenextlvl.commander.api.command.PlatformCommandRegistry;
import net.thenextlvl.commander.implementation.ProxyCommander;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public record ProxyPlatformCommandRegistry(ProxyCommander commander) implements PlatformCommandRegistry<CommandMeta> {

    @Override
    public Stream<String> getCommandNamespaces() {
        return getCommandManager().getAliases().stream();
    }

    @Override
    public Collection<CommandMeta> getCommands() {
        return getCommandManager().getAliases().stream()
                .map(s -> getCommand(s).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Optional<CommandMeta> getCommand(String literal) {
        return Optional.ofNullable(getCommandManager().getCommandMeta(literal));
    }

    @Override
    public void updateCommands() {
    }

    private CommandManager getCommandManager() {
        return commander.plugin().server().getCommandManager();
    }
}
