package net.thenextlvl.commander.velocity.implementation;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.commander.CommandFinder;
import net.thenextlvl.commander.velocity.CommanderPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.regex.Pattern;

@NullMarked
@RequiredArgsConstructor
public class ProxyCommandFinder implements CommandFinder {
    private final CommanderPlugin plugin;

    @Override
    public Set<String> findCommands(Pattern pattern) {
        return findCommands(plugin.server().getCommandManager().getAliases().stream(), pattern);
    }
}
