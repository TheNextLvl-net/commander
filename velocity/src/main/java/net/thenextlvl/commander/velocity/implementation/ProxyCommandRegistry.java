package net.thenextlvl.commander.velocity.implementation;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import net.thenextlvl.commander.CommandRegistry;

import java.util.Set;

@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public record ProxyCommandRegistry(CommanderPlugin plugin) implements CommandRegistry {

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

    @Override
    public void unregisterCommands() {

    }
}
