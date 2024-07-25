package net.thenextlvl.commander.implementation;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.commander.CommanderPlugin;
import net.thenextlvl.commander.api.CommandRegistry;

import java.util.Set;

@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public record ProxyCommandRegistry(CommanderPlugin plugin) implements CommandRegistry {

    private com.velocitypowered.api.command.CommandManager getCommandManager() {
        return plugin.server().getCommandManager();
    }

    @Override
    public Set<String> hiddenCommands() {
        return Set.of();
    }

    @Override
    public Set<String> unregisteredCommands() {
        return Set.of();
    }

    @Override
    public boolean hide(String command) {
        return false;
    }

    @Override
    public boolean isHidden(String command) {
        return false;
    }

    @Override
    public boolean isUnregistered(String command) {
        return false;
    }

    @Override
    public boolean register(String command) {
        return false;
    }

    @Override
    public boolean reveal(String command) {
        return false;
    }

    @Override
    public boolean unregister(String command) {
        return false;
    }
}
