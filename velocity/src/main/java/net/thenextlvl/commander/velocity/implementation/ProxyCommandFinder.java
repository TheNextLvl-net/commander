package net.thenextlvl.commander.velocity.implementation;

import net.thenextlvl.commander.CommandFinder;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.regex.Pattern;

@NullMarked
public class ProxyCommandFinder implements CommandFinder {
    private final CommanderPlugin plugin;

    public ProxyCommandFinder(CommanderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Set<String> findCommands(Pattern pattern) {
        return findCommands(plugin.server().getCommandManager().getAliases().stream(), pattern);
    }
}
