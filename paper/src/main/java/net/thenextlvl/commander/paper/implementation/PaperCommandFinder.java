package net.thenextlvl.commander.paper.implementation;

import net.thenextlvl.commander.CommandFinder;
import net.thenextlvl.commander.paper.PaperCommander;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.regex.Pattern;

@NullMarked
public class PaperCommandFinder implements CommandFinder {
    private final PaperCommander commander;

    public PaperCommandFinder(PaperCommander commander) {
        this.commander = commander;
    }

    @Override
    public Set<String> findCommands(Pattern pattern) {
        return findCommands(commander.getServer().getCommandMap().getKnownCommands().entrySet()
                .stream().mapMulti((entry, consumer) -> {
                    consumer.accept(entry.getKey());
                    entry.getValue().getAliases().forEach(consumer);
                }), pattern);
    }
}
