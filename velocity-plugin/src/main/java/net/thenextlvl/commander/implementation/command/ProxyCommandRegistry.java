package net.thenextlvl.commander.implementation.command;

import net.thenextlvl.commander.CommanderPlugin;
import net.thenextlvl.commander.api.command.CommandRegistry;
import net.thenextlvl.commander.implementation.ProxyCommander;

public class ProxyCommandRegistry extends CommandRegistry {
    public ProxyCommandRegistry(ProxyCommander commander, CommanderPlugin plugin) {
        super(commander, plugin.dataFolder().toFile());
    }
}
