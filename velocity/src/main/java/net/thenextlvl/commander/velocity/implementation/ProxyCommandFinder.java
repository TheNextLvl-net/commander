package net.thenextlvl.commander.velocity.implementation;

import net.thenextlvl.commander.CommandFinder;
import net.thenextlvl.commander.velocity.ProxyCommander;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.regex.Pattern;

@NullMarked
public class ProxyCommandFinder implements CommandFinder {
    private final ProxyCommander commander;

    public ProxyCommandFinder(ProxyCommander commander) {
        this.commander = commander;
    }

    @Override
    public Set<String> findCommands(Pattern pattern) {
        return findCommands(commander.server().getCommandManager().getAliases().stream(), pattern);
    }
}
