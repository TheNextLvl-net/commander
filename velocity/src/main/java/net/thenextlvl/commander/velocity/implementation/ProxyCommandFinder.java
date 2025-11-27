package net.thenextlvl.commander.velocity.implementation;

import net.thenextlvl.commander.CommonCommandFinder;
import net.thenextlvl.commander.velocity.ProxyCommander;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@NullMarked
public class ProxyCommandFinder extends CommonCommandFinder {
    private final ProxyCommander commander;

    public ProxyCommandFinder(ProxyCommander commander) {
        this.commander = commander;
    }

    @Override
    public Stream<String> findCommands(Pattern pattern) {
        return findCommands(commander.server().getCommandManager().getAliases().stream(), pattern);
    }
}
