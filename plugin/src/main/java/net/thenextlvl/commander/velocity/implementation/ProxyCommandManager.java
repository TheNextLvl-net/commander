package net.thenextlvl.commander.velocity.implementation;

import com.velocitypowered.api.command.CommandMeta;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import net.thenextlvl.commander.api.CommandInfo;
import net.thenextlvl.commander.api.platform.CommandManager;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public record ProxyCommandManager(ProxyCommander commander, CommanderPlugin plugin)
        implements CommandManager<CommandMeta> {

    @Override
    public Stream<String> getCommandNames() {
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
    public Stream<CommandMeta> getCommands(String query) {
        return getCommandNames()
                .filter(command -> CommandInfo.nameMatches(command, query))
                .map(s -> getCommand(s).orElse(null))
                .filter(Objects::nonNull);
    }

    @Override
    public Optional<CommandMeta> getCommand(String literal) {
        return Optional.ofNullable(getCommandManager().getCommandMeta(literal));
    }

    @Override
    public void updateCommands() {
    }

    private com.velocitypowered.api.command.CommandManager getCommandManager() {
        return plugin.server().getCommandManager();
    }
}
