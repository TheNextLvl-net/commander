package net.thenextlvl.commander.velocity.implementation;

import net.thenextlvl.commander.CommonCommandRegistry;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ProxyCommandRegistry extends CommonCommandRegistry {
    private final CommanderPlugin plugin;

    public ProxyCommandRegistry(CommanderPlugin plugin) {
        super(plugin.commons);
        this.plugin = plugin;
    }

    @Override
    protected boolean internalRegister(String command) {
        return true;
    }

    @Override
    protected boolean internalUnregister(String command) {
        plugin.server().getCommandManager().unregister(command);
        return true;
    }
}
